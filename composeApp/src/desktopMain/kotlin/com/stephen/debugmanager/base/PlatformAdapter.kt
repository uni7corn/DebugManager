package com.stephen.debugmanager.base

import com.stephen.debugmanager.data.PlatformType
import com.stephen.debugmanager.utils.LogUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class PlatformAdapter(private val singleInstanceApp: SingleInstanceApp) {

    init {
        println("PlatformAdapter init")
    }

    fun init() {
        createInitTempFile()
        singleInstanceApp.initCheckFileLock(lockFilePath)
    }

    companion object {
        // 路径分隔符
        val sp: String = File.separator

        // jvm工作目录
        // Windows可以显示exe的目录，另外2个平台显示的都是user目录
        private val workDirectory: String = System.getProperty("user.dir")

        // 桌面路径
        private val desktopPath = System.getProperty("user.home") + "${sp}Desktop"
        val desktopTempFolder = "$desktopPath${sp}AndroidTempFiles"

        // use目录下的配置文件目录
        private val userConfigFile = "${System.getProperty("user.home")}${sp}.debugmanagerTemp"
        val userLogConfigFile = "${userConfigFile}${sp}LogFiles"
        val userAndroidTempFiles = "${userConfigFile}${sp}AndroidDeviceTempFiles"

        val appVersion: String = System.getProperty("jpackage.app-version") ?: "DefaultVersion 1.0.0"

        val dataStoreFileName = "${userConfigFile}${sp}local_datastore.preferences_pb"

        val lockFilePath = "${userConfigFile}${sp}app.lock"
    }

    val localAdbPath =
        when (getPlatformType()) {
            PlatformType.WINDOWS, PlatformType.UNKNOWN ->
                "$workDirectory${sp}app${sp}resources${sp}scrcpy${sp}adb.exe"

            PlatformType.LINUX -> "adb"

            PlatformType.MAC -> "${System.getProperty("user.home")}${sp}Library${sp}Android${sp}sdk${sp}platform-tools${sp}adb"
        }

    val localScrcpyPath =
        when (getPlatformType()) {
            PlatformType.WINDOWS, PlatformType.UNKNOWN ->
                "$workDirectory${sp}app${sp}resources${sp}scrcpy${sp}scrcpy.exe"

            PlatformType.MAC, PlatformType.LINUX -> "scrcpy"
        }

    fun getUserTempFilePath() = userConfigFile

    suspend fun getAppInfoServiceApkPath() =
        when (getPlatformType()) {
            PlatformType.WINDOWS, PlatformType.UNKNOWN ->
                "\"$workDirectory${sp}app${sp}resources${sp}AppInfoService.apk\""

            PlatformType.LINUX ->
                // original result:
                // DebugManager: /opt/debugmanager/bin/DebugManager
                // need to switch to /opt/debugmanager//lib/app/resources/AppInfoService.apk
                executeCommandWithResult("whereis DebugManager").split(" ").drop(1).joinToString()
                    .split("/")
                    .dropLast(2)
                    .joinToString("/") + "${sp}lib${sp}app${sp}resources${sp}AppInfoService.apk"

            PlatformType.MAC ->
                "/Applications${sp}DebugManager.app${sp}Contents${sp}app${sp}resources${sp}AppInfoService.apk"
        }

    /**
     * 创建配置文件缓存文件夹
     */
    private fun createInitTempFile() {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                // 创建本地log文件夹
                val localLogFile = Paths.get("${userLogConfigFile}${sp}thisisaemptyfile")
                Files.createDirectories(localLogFile.parent)
                // 创建android拉取图标等数据文件夹
                val androidTemp = Paths.get("${userAndroidTempFiles}${sp}thisisaemptyfile")
                Files.createDirectories(androidTemp.parent)
                // 创建桌面的缓存文件
                val desktopTemp = Paths.get("${desktopTempFolder}${sp}thisisaemptyfile")
                Files.createDirectories(desktopTemp.parent)
                // 目录创建完成，程序开启
                LogUtils.printLog("\n\n\n=====>Application start<======")
            }.onFailure { e ->
                LogUtils.printLog("创建配置文件失败：${e.message}")
            }
        }
    }

    /**
     * 获取当前平台类型
     */
    private fun getPlatformType(): PlatformType {
        val osName = System.getProperty("os.name").lowercase(Locale.getDefault())
        return when {
            osName.contains("win") -> PlatformType.WINDOWS
            osName.contains("mac") -> PlatformType.MAC
            osName.contains("nix") || osName.contains("nux") || osName.contains("aix") -> PlatformType.LINUX
            else -> PlatformType.UNKNOWN
        }
    }

    /**
     * 执行终端命令
     */
    fun executeTerminalCommand(command: String) {
        runCatching {
            Runtime.getRuntime().exec(command)
        }.onFailure { e ->
            LogUtils.printLog("执行出错：${e.message}", LogUtils.LogLevel.ERROR)
        }
    }

    /**
     * 执行命令，获取输出
     */
    suspend fun executeCommandWithResult(command: String) = withContext(Dispatchers.IO) {
        val processBuilder = ProcessBuilder(*command.split(" ").toTypedArray())
        val process = processBuilder.start()

        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val output = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            output.append(line).append("\n")
        }
        // 等待进程结束
        process.waitFor()
        // 关闭输入流
        reader.close()
        output.toString()
    }

    fun openFolder(path: String) {
        when (getPlatformType()) {
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
}

fun getSystemEnv(key: String)= System.getenv(key).also {
    println("getSystemEnv: $key = $it")
}