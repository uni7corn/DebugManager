package com.stephen.debugmanager.base

import com.stephen.debugmanager.data.LanguageState
import com.stephen.debugmanager.data.PlatformType
import com.stephen.debugmanager.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class PlatformAdapter(private val singleInstanceApp: SingleInstanceApp) {

    fun init(onSingleAppRunning: (Boolean) -> Unit) {
        createInitTempFile()
        singleInstanceApp.initCheckFileLock(lockFilePath, onSingleAppRunning)
    }

    companion object {
        // 路径分隔符
        val sp: String = File.separator

        val computerUserName: String? = System.getProperty("user.name")

        val osName = System.getProperty("os.name").lowercase(Locale.getDefault())

        val platformType = if (osName.contains("win")) PlatformType.WINDOWS
        else if (osName.contains("mac")) PlatformType.MAC
        else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) PlatformType.LINUX
        else PlatformType.UNKNOWN

        // 终端清屏命令
        val clearCommandList = listOf<String>("cls", "clear")

        // jvm工作目录
        // Windows可以显示exe的目录，另外2个平台显示的都是user目录
        private val workDirectory: String = System.getProperty("user.dir")

        // use目录下的配置文件目录
        private val userConfigFile = "${System.getProperty("user.home")}${sp}.debugmanagerTemp"
        val userLogConfigFile = "${userConfigFile}${sp}LogFiles"
        val userAndroidTempFiles = "${userConfigFile}${sp}AndroidDeviceTempFiles"

        // 拉取Android文件缓存文件夹路径
        val pulledTempFolder = "$userConfigFile${sp}PulledTempFiles"

        val appVersion: String = System.getProperty("jpackage.app-version") ?: "DefaultVersion 1.0.0"

        val dataStoreFileName = "${userConfigFile}${sp}local_datastore.preferences_pb"

        val lockFilePath = "${userConfigFile}${sp}app.lock"

        val systemLocale: Locale = Locale.getDefault() ?: Locale.US
        val systemLanguage = systemLocale.toString().split("_").first()
    }

    val localAdbPath =
        when (platformType) {
            PlatformType.WINDOWS ->
                "$workDirectory${sp}app${sp}resources${sp}scrcpy${sp}adb.exe"

            PlatformType.LINUX, PlatformType.UNKNOWN -> "${sp}opt${sp}debugmanager${sp}lib${sp}app${sp}resources${sp}scrcpy${sp}adb"

            PlatformType.MAC -> "${sp}Applications${sp}DebugManager.app${sp}Contents${sp}app${sp}resources${sp}scrcpy${sp}adb"
        }

    val localScrcpyPath =
        when (platformType) {
            PlatformType.WINDOWS ->
                "$workDirectory${sp}app${sp}resources${sp}scrcpy${sp}scrcpy.exe"

            PlatformType.LINUX, PlatformType.UNKNOWN -> "${sp}opt${sp}debugmanager${sp}lib${sp}app${sp}resources${sp}scrcpy${sp}scrcpy"

            PlatformType.MAC -> "${sp}Applications${sp}DebugManager.app${sp}Contents${sp}app${sp}resources${sp}scrcpy${sp}scrcpy"
        }

    fun getUserTempFilePath() = userConfigFile

    suspend fun getAppInfoServerDexPath() =
        when (platformType) {
            PlatformType.WINDOWS ->
                "\"$workDirectory${sp}app${sp}resources${sp}aya.dex\""

            PlatformType.LINUX, PlatformType.UNKNOWN ->
                // original result:
                // DebugManager: /opt/debugmanager/bin/DebugManager
                // need to switch to /opt/debugmanager//lib/app/resources/AppInfoService.apk
                executeCommandWithResult("whereis DebugManager").split(" ").drop(1).joinToString()
                    .split("/")
                    .dropLast(2)
                    .joinToString("/") + "${sp}lib${sp}app${sp}resources${sp}aya.dex"

            PlatformType.MAC ->
                "/Applications${sp}DebugManager.app${sp}Contents${sp}app${sp}resources${sp}aya.dex"
        }

    /**
     * 创建配置文件缓存文件夹
     */
    private fun createInitTempFile() {
        runCatching {
            // 创建本地log文件夹
            val localLogFile = Paths.get("${userLogConfigFile}${sp}thisisaemptyfile")
            Files.createDirectories(localLogFile.parent)
            // 创建android拉取图标等数据文件夹
            val androidTemp = Paths.get("${userAndroidTempFiles}${sp}thisisaemptyfile")
            Files.createDirectories(androidTemp.parent)
            // 创建桌面的缓存文件
            val desktopTemp = Paths.get("${pulledTempFolder}${sp}thisisaemptyfile")
            Files.createDirectories(desktopTemp.parent)
            // 进程锁文件
            val lockFile = Paths.get(lockFilePath)
            Files.createDirectories(lockFile.parent)
            // 目录创建完成，程序开启
            LogUtils.printLog("\n\n\n=====>Application start<======")
        }.onFailure { e ->
            LogUtils.printLog("创建配置文件失败：${e.message}")
        }
    }

    /**
     * 执行终端命令
     */
    fun executeTerminalCommand(command: String): Process? {
        runCatching {
            return Runtime.getRuntime().exec(command)
        }.onFailure { e ->
            LogUtils.printLog("执行出错：${e.message}", LogUtils.LogLevel.ERROR)
        }
        return null
    }

    /**
     * 执行命令，直接获取输出
     * @param isRemoveReturn 是否移除换行符
     * @return 命令执行结果
     */
    suspend fun executeCommandWithResult(command: String, isRemoveReturn: Boolean = true): String =
        withContext(Dispatchers.IO) {
            var result = ""
            procecssStreamResultBuilder(command).collect {
                result = if (isRemoveReturn)
                    it.replace(Regex("\\r?\\n"), "")
                else
                    it
            }
            result
        }

    private fun procecssStreamResultBuilder(command: String): Flow<String> = callbackFlow {
        try {
            val processBuilder = ProcessBuilder(*command.split(" ").toTypedArray())
            val process = processBuilder.start()

            val outputReader = BufferedReader(InputStreamReader(process.inputStream))
            val errorReader = BufferedReader(InputStreamReader(process.errorStream))

            val outputBuilder = StringBuilder()
            var line: String?
            while (outputReader.readLine().also { line = it } != null) {
                outputBuilder.append(line).append("\n")
            }

            val errorBuilder = StringBuilder()
            var errorLine: String?
            while (errorReader.readLine().also { errorLine = it } != null) {
                errorBuilder.append(errorLine).append("\n")
                trySend(errorBuilder.toString())
            }
            // 拼接最终结果
            val overallResult = StringBuilder()
            if (outputBuilder.isNotEmpty()) {
                overallResult.append(outputBuilder)
            }
            if (errorBuilder.isNotEmpty()) {
                overallResult.append(errorBuilder)
            }
            // 发送最终的完整结果
            trySend(overallResult.toString())
            close()
        } catch (e: Exception) {
            // 处理异常
            close(e)
        }
    }

    /**
     * 单开一个terminal窗口
     */
    suspend fun openSingleTerminalWindow() = withContext(Dispatchers.IO) {
        // 创建一个ProcessBuilder实例
        // 第一个参数是可执行文件，后面的参数是传递给它的命令行参数
        // /C：执行命令后关闭窗口
        // /K：执行命令后保持窗口打开 (我们想要的)
        // dir：你想要执行的命令
        val builder = ProcessBuilder("cmd.exe", "/C", "start", "cmd.exe", "/K", "$localAdbPath --version")

        // 设置工作目录（可选）
        // builder.directory(new File("C:\\"))

        // 设置进程环境变量（可选）
        // Map<String, String> env = builder.environment()
        // env.put("MY_VAR", "myValue")

        try {
            // 启动进程
            val process = builder.start()

            LogUtils.printLog("===========>CMD started<============")

            // 可以选择等待进程执行结束
            val exitCode = process.waitFor()
            LogUtils.printLog("process exit code: $exitCode")
        } catch (e: IOException) {
            LogUtils.printLog("open cmd window failed: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * 打开Desktop上某个文件夹
     */
    fun openFolder(path: String) {
        when (platformType) {
            PlatformType.WINDOWS, PlatformType.UNKNOWN -> {
                executeTerminalCommand("explorer.exe $path")
            }

            PlatformType.MAC -> {
                executeTerminalCommand("open $path")
            }

            PlatformType.LINUX -> {
                executeTerminalCommand("xdg-open $path")
            }
        }
    }

    /**
     * 复制到剪切板
     */
    fun copyToClipboard(text: String) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val clip = StringSelection(text)
        clipboard.setContents(clip, null)
    }

    /**
     * 调用系统警示音
     */
    fun callSystemAlert() {
        runCatching {
            Toolkit.getDefaultToolkit().beep()
        }.onFailure {
            LogUtils.printLog("callSystemAlert failed: ${it.message}", LogUtils.LogLevel.ERROR)
        }
    }
}

/**
 * 获取系统环境变量
 * @param key 环境变量的key
 * @return 环境变量的值
 */
@Suppress("unused")
fun getSystemEnv(key: String): String? = System.getenv(key).also {
    LogUtils.printLog("getSystemEnv: $key = $it")
}

/**
 * 获取语言的Locale
 * @param languageState 语言状态
 * @return 语言的Locale
 */
fun getLanguageLocale(languageState: Int): Locale {
    return when (languageState) {
        LanguageState.CHINESE -> Locale("zh", "CN")
        LanguageState.ENGLISH -> Locale("en", "US")
        LanguageState.RUSSIAN -> Locale("ru", "RU")
        LanguageState.HINDI -> Locale("hi", "IN")
        LanguageState.SPANISH -> Locale("es", "ES")
        LanguageState.FRENCH -> Locale("fr", "FR")
        LanguageState.GERMAN -> Locale("de", "DE")
        LanguageState.KOREAN -> Locale("ko", "KR")
        LanguageState.JAPANESE -> Locale("ja", "JP")
        LanguageState.ARABIC -> Locale("ar", "SA")
        else -> PlatformAdapter.systemLocale
    }
}

/**
 * 获取语言的Code
 * @param languageState 语言状态
 * @return 语言的Code
 */
fun getLanguageCode(languageState: Int): String {
    return when (languageState) {
        LanguageState.CHINESE -> "zh"
        LanguageState.ENGLISH -> "en"
        LanguageState.RUSSIAN -> "ru"
        LanguageState.HINDI -> "hi"
        LanguageState.SPANISH -> "es"
        LanguageState.FRENCH -> "fr"
        LanguageState.GERMAN -> "de"
        LanguageState.KOREAN -> "ko"
        LanguageState.JAPANESE -> "ja"
        LanguageState.ARABIC -> "ar"
        else -> PlatformAdapter.systemLanguage
    }
}