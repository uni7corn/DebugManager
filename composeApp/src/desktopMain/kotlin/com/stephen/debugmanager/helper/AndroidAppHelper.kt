package com.stephen.debugmanager.helper

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import com.stephen.debugmanager.base.AdbClient
import com.stephen.debugmanager.base.PlatformAdapter
import com.stephen.debugmanager.data.PackageFilter
import com.stephen.debugmanager.data.bean.AppInfoItemData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import com.stephen.debugmanager.utils.LogUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

class AndroidAppHelper(private val adbClient: AdbClient, private val platformAdapter: PlatformAdapter) {

    suspend fun initAppInfoServer() = withContext(Dispatchers.IO) {
        pushDexFile()
        launchServer()
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            delay(5000L)
            saveAllAppInfo()
        }
    }

    private suspend fun saveAllAppInfo() = withContext(Dispatchers.IO) {
        // 检查AppInfoService是否在运行
        val packageList = getInstalledApps(false)
        requestSaveAllAppInfo(packageList)
    }

    suspend fun getInstalledApps(isIgnoreSystemApps: Boolean) = adbClient
        .getAndroidShellExecuteResult(
            adbClient.serial,
            if (isIgnoreSystemApps) "pm list package -3"
            else "pm list package", false
        )
        .split("\n")
        .filter { it.isNotEmpty() }
        .map { it.replace("package:", "") }

    private suspend fun pushDexFile() = withContext(Dispatchers.IO) {
        if (adbClient.getAndroidShellExecuteResult(adbClient.serial, "ls /data/local/tmp/aya/")
                .apply { LogUtils.printLog("check AppInfoService Process Running result:$this") }
                .contains("aya.dex")
        ) {
            LogUtils.printLog("DEX file exists")
        } else {
            LogUtils.printLog("No DEX file exists, push a new one")
            val appInfoServerDexPath = platformAdapter.getAppInfoServerDexPath()
            LogUtils.printLog("pushAppInfoServer:$appInfoServerDexPath")
            platformAdapter.executeCommandWithResult("${platformAdapter.localAdbPath} -s ${adbClient.serial} push $appInfoServerDexPath /data/local/tmp/aya/aya.dex")
        }
    }

    private suspend fun launchServer() = withContext(Dispatchers.IO) {
        // 判断Server是否在运行
        if (adbClient.getAndroidShellExecuteResult(adbClient.serial, "cat /proc/net/unix | grep \"@aya\"")
                .apply { LogUtils.printLog("check AppInfoService Process Running result:$this") }
                .isNotEmpty()
        ) {
            LogUtils.printLog("Server is running")
        } else {
            LogUtils.printLog("Server is not running")
            // 启动Server
            platformAdapter.executeTerminalCommand("${platformAdapter.localAdbPath} -s ${adbClient.serial} shell CLASSPATH=/data/local/tmp/aya/aya.dex app_process /system/bin io.liriliri.aya.Server")
            delay(500L)
            // 转发端口
            platformAdapter.executeTerminalCommand("${platformAdapter.localAdbPath} -s ${adbClient.serial} forward tcp:1234 localabstract:aya")
        }
    }

    suspend fun pullAppInfoToComputer(whenFilePulled: suspend () -> Unit) = withContext(Dispatchers.IO) {
        LogUtils.printLog("pullAppInfoToComputer")
        initAppInfoServer()
        val androidPath = "/data/local/tmp/aya/icons"
        platformAdapter.executeCommandWithResult("${platformAdapter.localAdbPath} -s ${adbClient.serial} pull $androidPath ${PlatformAdapter.userAndroidTempFiles}")
        whenFilePulled()
    }

    /**
     * 循环读取图标文件，直到存在为止
     */
    fun getIconFilePath(packageName: String) =
        "${PlatformAdapter.userAndroidTempFiles}${PlatformAdapter.sp}icons${PlatformAdapter.sp}$packageName.png"

    fun analyzeAppLabel(): Map<String, AppInfoItemData> {
        val path =
            "${PlatformAdapter.userAndroidTempFiles}${PlatformAdapter.sp}appInfo${PlatformAdapter.sp}apps_info_test.json"
        val packageLabelMap = mutableMapOf<String, AppInfoItemData>()
        // 读取到文件为止
        val file = File(path)
        if (file.exists()) {
            // 解析JSON字符串为AppInfoItemData对象
            val jsonString = file.readText()
            val json = Json {
                ignoreUnknownKeys = true  // 关键配置：忽略未知key
                coerceInputValues = true   // 可选：自动转换不匹配的类型（如数字转字符串）
            }
            val appInfoList = json.decodeFromString<List<AppInfoItemData>>(jsonString)
            appInfoList.forEach { appInfo ->
                packageLabelMap[appInfo.packageName] = appInfo
            }
        }
        return packageLabelMap
    }


    @Serializable
    data class GetPackageInfosParams(
        val packageNames: List<String>
    )

    @Serializable
    data class Request<T>(
        val id: String,
        val method: String,
        val params: T
    )

    /**
     * 修改server适配DebugManager的原有流程，前置操作：
     * 1. adb push aya.dex /data/local/tmp/aya/aya.dex
     * 2. CLASSPATH=/data/local/tmp/aya/aya.dex app_process /system/bin io.liriliri.aya.Server
     * 3. adb forward tcp:1234 localabstract:aya
     * 4. 运行请求
     * 5. pull 出文件到Desktop
     * 6. 拿取png显示
     */

    fun requestSaveAllAppInfo(appList: List<String>) {
        val host = "localhost"
        val port = 1234

        try {
            // 创建一个 Socket 并连接到主机和端口
            Socket(host, port).use { socket ->
                println("Successfully connected to $host:$port")

                // 获取输入和输出流
                val writer = PrintWriter(socket.getOutputStream(), true)
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

                // 1. 构建参数对象
                val params = GetPackageInfosParams(appList)
                // 2. 构建请求对象，params字段直接使用上面创建的对象
                val request = Request(
                    id = "214",
                    method = "saveAllInfoToFile",
                    params = params
                )
                // 3. 将整个请求对象序列化为 JSON 字符串
                val jsonString = Json.encodeToString(value = request)
                // 发送请求，加上换行符作为分隔符
                writer.println(jsonString)
                println("Sent request: $request")
                // 读取服务器响应
                val responseString = reader.readLine()
                if (responseString != null) {
                    println("Received response: $responseString")
                } else {
                    println("Received empty response")
                }
            }
        } catch (e: Exception) {
            System.err.println("An error occurred: ${e.message}")
            e.printStackTrace()
        }
    }
}