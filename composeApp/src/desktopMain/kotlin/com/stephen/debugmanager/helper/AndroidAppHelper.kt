package com.stephen.debugmanager.helper

import com.stephen.debugmanager.base.AdbClient
import com.stephen.debugmanager.base.PlatformAdapter
import com.stephen.debugmanager.data.bean.AyaRequest
import com.stephen.debugmanager.data.bean.AyaResponse
import com.stephen.debugmanager.data.bean.PackageListParams
import com.stephen.debugmanager.utils.LogUtils
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.nio.charset.StandardCharsets


class AndroidAppHelper(private val adbClient: AdbClient, private val platformAdapter: PlatformAdapter) {

    /**
     * 初始化AppInfoServer，单次执行
     */
    suspend fun initAppInfoServer() = withContext(Dispatchers.IO) {
        pushDexFile()
        launchServer()
        delay(3000L)
        // 延时转发端口，链接Socket
        platformAdapter.executeCommandWithResult("${platformAdapter.localAdbPath} -s ${adbClient.serial} forward tcp:1234 localabstract:aya")
        connect()
    }

    /**
     * 获取安装的app列表，可以选择包含和忽略系统应用
     */
    suspend fun getInstalledApps(showSystemApps: Boolean) = adbClient
        .getAndroidShellExecuteResult(
            adbClient.serial,
            if (showSystemApps) "pm list package"
            else "pm list package -3",
            false
        ).split('\n')
        .map { it.trim() } // 移除每行首尾的空白
        .filter { it.startsWith("package:") } // 过滤出以 "package:" 开头的行
        .map { it.substringAfter("package:") } // 提取 "package:" 后面的部分
        .filter { it.isNotEmpty() } // 过滤掉空字符串
        .also {
            LogUtils.printLog("getInstalledApps lenth: ${it.size}")
        }

    /**
     * 推送aya.dex文件到Android设备的/data/local/tmp/aya/目录下
     */
    private suspend inline fun pushDexFile() = withContext(Dispatchers.IO) {
        if (platformAdapter.executeCommandWithResult("${platformAdapter.localAdbPath} -s ${adbClient.serial} shell ls /data/local/tmp/aya/")
                .apply { LogUtils.printLog("check dex file existence result:$this") }
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

    /**
     * 通过CLASSPATH启动Server
     * 转发到Desktop本地端口
     * 需要单开一个线程，保证会话不中断
     */
    private fun launchServer() {
        CoroutineScope(Dispatchers.IO).launch {
            // 判断Server是否在运行
            if (adbClient.getAndroidShellExecuteResult(adbClient.serial, "cat /proc/net/unix | grep \"@aya\"")
                    .apply { LogUtils.printLog("check AppInfoService Process Running result:$this") }
                    .isNotEmpty()
            ) {
                LogUtils.printLog("Server is running")
            } else {
                LogUtils.printLog("Server is not running")
                // 启动Server
                platformAdapter.executeCommandWithResult("${platformAdapter.localAdbPath} -s ${adbClient.serial} shell CLASSPATH=/data/local/tmp/aya/aya.dex app_process /system/bin io.liriliri.aya.Server")
            }
        }
    }

    private lateinit var socket: Socket
    private lateinit var writer: PrintWriter
    private lateinit var reader: BufferedReader
    val host = "localhost"
    val port = 1234

    fun connect() {
        if (this::socket.isInitialized && !socket.isClosed) {
            return // 已经连接，无需重复连接
        }
        LogUtils.printLog("Connecting to $host:$port...")
        socket = Socket(host, port)
        writer = PrintWriter(socket.getOutputStream(), true)
        reader = BufferedReader(InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
        LogUtils.printLog("Connected.")
    }

    @Suppress("Unused")
    fun disconnect() {
        if (this::socket.isInitialized) {
            socket.close()
            LogUtils.printLog("Disconnected.")
        }
    }

    fun sendRequest(ayaRequest: AyaRequest) {
        if (!this::socket.isInitialized || socket.isClosed) {
            throw IllegalStateException("Client is not connected. Call connect() first.")
        }
        val jsonString = Json.encodeToString(value = ayaRequest)
        writer.println(jsonString)
//        LogUtils.printLog("Sent request: $jsonString")
    }

    fun readResponse(): AyaResponse {
        if (!this::socket.isInitialized || socket.isClosed) {
            throw IllegalStateException("Client is not connected.")
        }
        val response: String = reader.readLine() ?: ""
        val json = Json {
            ignoreUnknownKeys = true
        }
        return json.decodeFromString<AyaResponse>(response)
    }

    /**
     * 请求获取应用信息，根据包名获取
     */
    suspend fun requestPackageInfo(packageName: String) = withContext(Dispatchers.IO) {
        val params = PackageListParams(listOf(packageName))
        val ayaRequest = AyaRequest(
            id = "214",
            method = "getPackageInfos",
            params = params
        )
        sendRequest(ayaRequest)
        val response = readResponse()
//        LogUtils.printLog("response:$response")
        response
    }

    /**
     * 全量执行后，拉取一次所有app的图标png文件
     */
    suspend fun pullAppIconsToComputer(whenIconFilePulled: suspend () -> Unit) = withContext(Dispatchers.IO) {
        LogUtils.printLog("pullAppInfoToComputer")
        val androidPath = "/data/local/tmp/aya/icons"
        platformAdapter.executeCommandWithResult("${platformAdapter.localAdbPath} -s ${adbClient.serial} pull $androidPath ${PlatformAdapter.userAndroidTempFiles}")
        whenIconFilePulled()
    }
}