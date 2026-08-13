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
import java.nio.file.attribute.PosixFilePermission
import java.util.*

class PlatformAdapter(private val singleInstanceApp: SingleInstanceApp) {

    fun init(onSingleAppRunning: (Boolean) -> Unit) {
        ensureUserDataDirectories()
        ensureExecutablePermissions()
        singleInstanceApp.initCheckFileLock(lockFilePath, onSingleAppRunning)
    }

    companion object {

        // ===== 平台判定 =====

        /** 系统路径分隔符（Windows 为 '\'，macOS/Linux 为 '/'） */
        val sp: String = File.separator

        /** 当前系统登录用户名，用于终端提示符展示 */
        val computerUserName: String? = System.getProperty("user.name")

        /** 操作系统名（小写），仅用于下方 platformType 判定 */
        private val osName = System.getProperty("os.name").lowercase(Locale.getDefault())

        val platformType = when {
            osName.contains("win") -> PlatformType.WINDOWS
            osName.contains("mac") -> PlatformType.MAC
            osName.contains("nix") || osName.contains("nux") || osName.contains("aix") -> PlatformType.LINUX
            else -> PlatformType.UNKNOWN
        }

        /** 终端清屏命令（Windows 为 cls，macOS/Linux 为 clear） */
        val clearCommandList = listOf("cls", "clear")

        // ===== 运行环境 =====

        /** JVM 工作目录；仅开发态（gradle run）回退定位资源时使用，打包态不依赖它 */
        private val workDirectory: String = System.getProperty("user.dir")

        /** 应用版本：打包态由 jpackage 注入，开发态显示 dev */
        val appVersion: String = System.getProperty("jpackage.app-version") ?: "dev"

        // ===== 用户数据 / 缓存 / 日志目录（按 OS 标准位置） =====
        // 配置、缓存、日志分别落到各平台约定目录，避免自建 ~/.debugmanagerTemp。

        /** 用户主目录 */
        private val homeDir: String = System.getProperty("user.home")

        /** 配置目录：DataStore 偏好、单实例锁 */
        private val userConfigDir: String = when (platformType) {
            PlatformType.WINDOWS ->
                (System.getenv("APPDATA")?.takeIf { it.isNotBlank() } ?: homeDir) + "${sp}DebugManager"
            PlatformType.MAC ->
                "$homeDir${sp}Library${sp}Application Support${sp}DebugManager"
            else ->
                (System.getenv("XDG_CONFIG_HOME")?.takeIf { it.isNotBlank() } ?: "$homeDir${sp}.config") + "${sp}DebugManager"
        }

        /** 缓存目录：设备拉取的临时数据与文件 */
        private val userCacheDir: String = when (platformType) {
            PlatformType.WINDOWS ->
                (System.getenv("LOCALAPPDATA")?.takeIf { it.isNotBlank() } ?: homeDir) + "${sp}DebugManager${sp}Cache"
            PlatformType.MAC ->
                "$homeDir${sp}Library${sp}Caches${sp}DebugManager"
            else ->
                (System.getenv("XDG_CACHE_HOME")?.takeIf { it.isNotBlank() } ?: "$homeDir${sp}.cache") + "${sp}DebugManager"
        }

        /** 日志目录 */
        private val userLogDir: String = when (platformType) {
            PlatformType.WINDOWS ->
                (System.getenv("LOCALAPPDATA")?.takeIf { it.isNotBlank() } ?: homeDir) + "${sp}DebugManager${sp}Logs"
            PlatformType.MAC ->
                "$homeDir${sp}Library${sp}Logs${sp}DebugManager"
            else ->
                (System.getenv("XDG_STATE_HOME")?.takeIf { it.isNotBlank() } ?: "$homeDir${sp}.local${sp}state") + "${sp}DebugManager"
        }

        /** 从 Android 设备拉取的临时数据目录（应用图标等） */
        val userAndroidTempFiles = "$userCacheDir${sp}AndroidDeviceTempFiles"

        /** 从设备 Pull 下来的文件缓存目录 */
        val pulledTempFolder = "$userCacheDir${sp}PulledTempFiles"

        /** 应用偏好文件（主题 / 语言 / AI 模型，DataStore） */
        val dataStoreFileName = "$userConfigDir${sp}local_datastore.preferences_pb"

        /** 单实例文件锁 */
        val lockFilePath = "$userConfigDir${sp}app.lock"

        /** 启动时需确保存在的所有目录（幂等创建） */
        private val userDataDirs = listOf(
            userConfigDir,
            userCacheDir,
            userLogDir,
            userAndroidTempFiles,
            pulledTempFolder,
        )

        /**
         * 必须在任何 Logger 初始化之前调用：创建日志目录并把路径注入系统属性，
         * 供 log4j2.xml 的 ${sys:debugmanager.log.dir} 使用。
         */
        fun configureLogging() {
            runCatching { Files.createDirectories(Paths.get(userLogDir)) }
            System.setProperty("debugmanager.log.dir", userLogDir)
        }

        // ===== 本地化 =====

        val systemLocale: Locale = Locale.getDefault() ?: Locale.US
        val systemLanguage = systemLocale.toString().split("_").first()
    }

    /**
     * 应用内容目录：打包后本 class 从 <安装目录>/app 下的 jar 加载
     * （macOS 为 *.app/Contents/app，Linux 为 /opt/debugmanager/lib/app，Windows 为 <安装目录>/app），
     * 该 jar 所在目录即 app 内容目录，adb/scrcpy 等资源统一放在它下面的 resources/。
     * 开发态（gradle run）class 从 build/classes 加载，此值不可用，交由 resolveResourcePath 回退 user.dir。
     */
    private val appContentDir: String? = runCatching {
        val url = PlatformAdapter::class.java.protectionDomain?.codeSource?.location
            ?: return@runCatching null
        Paths.get(url.toURI()).parent?.toString()
    }.getOrNull()

    /**
     * 解析本机资源文件的绝对路径：按候选顺序取第一个存在的文件，都不存在则返回首选（动态路径，不写死安装目录）。
     * 三平台统一布局为 <资源根>/resources/…，仅 Windows 的可执行文件带 .exe 后缀。
     */
    private fun resolveResourcePath(relativeInResources: String): String {
        val platformDir = when (platformType) {
            PlatformType.WINDOWS -> "windows"
            PlatformType.MAC -> "macos"
            else -> "linux"
        }
        val candidates = buildList {
            // 1) 打包态：app 内容目录下的 resources/
            appContentDir?.let { add("$it${sp}resources${sp}$relativeInResources") }
            // 2) 开发态：仓库 resources/{platform}/（相对 user.dir 或上一级）
            add("$workDirectory${sp}resources${sp}$platformDir${sp}$relativeInResources")
            add("$workDirectory${sp}..${sp}resources${sp}$platformDir${sp}$relativeInResources")
        }
        return candidates.firstOrNull { Files.exists(Paths.get(it)) } ?: candidates.first()
    }

    private fun binaryPath(name: String): String =
        resolveResourcePath("scrcpy${sp}${if (platformType == PlatformType.WINDOWS) "$name.exe" else name}")

    val localAdbPath: String = binaryPath("adb")

    val localScrcpyPath: String = binaryPath("scrcpy")

    fun getUserTempFilePath() = userCacheDir

    suspend fun getAppInfoServerDexPath(): String = resolveResourcePath("aya.dex")

    /**
     * 启动时确保用户数据 / 缓存目录存在（幂等：已存在则跳过）。
     */
    private fun ensureUserDataDirectories() {
        runCatching {
            userDataDirs.forEach { Files.createDirectories(Paths.get(it)) }
            LogUtils.printLog("\n\n\n=====>Application start<======")
        }.onFailure { e ->
            LogUtils.printLog("创建用户数据目录失败：${e.message}", LogUtils.LogLevel.ERROR)
        }
    }

    /**
     * 确保本机 adb/scrcpy 二进制具备可执行权限。
     * jpackage 在 macOS/Linux 打包时可能丢失文件的 x 位，导致命令执行报 Permission denied；
     * 这里在启动阶段自动修复，避免依赖用户手动 chmod。Windows 使用 .exe，无需处理。
     */
    fun ensureExecutablePermissions() {
        if (platformType == PlatformType.WINDOWS) return
        listOf(localAdbPath, localScrcpyPath).forEach { path ->
            runCatching {
                val file = Paths.get(path)
                if (Files.exists(file)) {
                    Files.setPosixFilePermissions(
                        file,
                        setOf(
                            PosixFilePermission.OWNER_READ,
                            PosixFilePermission.OWNER_WRITE,
                            PosixFilePermission.OWNER_EXECUTE,
                            PosixFilePermission.GROUP_READ,
                            PosixFilePermission.GROUP_EXECUTE,
                            PosixFilePermission.OTHERS_READ,
                            PosixFilePermission.OTHERS_EXECUTE,
                        )
                    )
                    LogUtils.printLog("ensureExecutablePermissions: $path")
                }
            }.onFailure { e ->
                LogUtils.printLog("ensureExecutablePermissions failed: $path -> ${e.message}", LogUtils.LogLevel.ERROR)
            }
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