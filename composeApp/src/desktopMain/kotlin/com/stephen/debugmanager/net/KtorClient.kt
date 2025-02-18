package com.stephen.debugmanager.net

import com.stephen.debugmanager.data.bean.AiRequestData
import com.stephen.debugmanager.data.bean.KimiResult
import com.stephen.debugmanager.data.bean.RequestMessage
import com.stephen.debugmanager.utils.LogUtils
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.PrintStream
import java.nio.charset.StandardCharsets

class KtorClient {

    private val client = HttpClient(CIO) {
        install(Logging) {
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    val url = "https://api.moonshot.cn/v1/chat/completions"

    val apikey = "sk-XXXXXXXXXXXXXXXXXX"

    suspend fun getChatResult() = withContext(Dispatchers.IO) {
        client.post(url) {
            // 配置请求头
            headers {
                append("Content-Type", "application/json")
                append("Authorization", "Bearer $apikey")
            }
            setBody(
                AiRequestData(
                    model = "moonshot-v1-32k-vision-preview",
                    max_tokens = 2048,
                    temperature = 0.3,
                    stream = false,
                    messages = listOf(
                        RequestMessage("你是一个人工智能系统，可以根据用户的输入来返回生成式的回复", "system"),
                        RequestMessage("帮我生成一个100字左右的冷笑话", "user")
                    )
                )
            )
        }.body<KimiResult>()
    }

    fun getChatResultTest() {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                val result = getChatResult()
                System.setOut(PrintStream(System.out, true, StandardCharsets.UTF_8))
                result.choices.forEach { choice ->
                    println(choice.message.content)
                }
            }.onFailure { e ->
                LogUtils.printLog(e.message.toString(), LogUtils.LogLevel.ERROR)
            }
        }
    }

    fun release() {
        client.close()
    }
}
