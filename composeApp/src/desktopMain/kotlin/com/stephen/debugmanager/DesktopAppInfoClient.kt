package com.stephen.debugmanager

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket


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

fun main() {
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
            val params = GetPackageInfosParams(
                packageNames = listOf("com.openai.chatgpt", "com.meizu.picker")
            )

// 2. 构建请求对象，params字段直接使用上面创建的对象
            val request = Request(
                id = "214",
                method = "saveAllInfoToFile",
                params = params
            )

// 3. 将整个请求对象序列化为 JSON 字符串
            val jsonString = Json.encodeToString(request)

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