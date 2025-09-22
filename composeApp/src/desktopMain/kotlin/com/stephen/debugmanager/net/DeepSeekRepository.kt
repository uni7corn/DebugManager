package com.stephen.debugmanager.net

import com.stephen.debugmanager.data.bean.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlin.coroutines.cancellation.CancellationException

class DeepSeekRepository(private val ktorClient: KtorClient) {

    companion object {
        const val BASE_URL = "https://api.deepseek.com"

        const val COMMON_SYSTEM_PROMT = "你是一个人工智能系统，可以根据用户的输入来返回生成式的回复"

        // 改为从环境变量获取API_KEY
        val API_KEY: String = System.getenv("DEEPSEEK_API_KEY") ?: "sk-Xxxxxxxxxxxx"

        const val MODEL_NAME = "deepseek-chat"
    }

    suspend fun chatWithDeepSeek(text: String) = withContext(Dispatchers.IO) {
        ktorClient.client.post("${BASE_URL}/chat/completions") {
            // 配置请求头
            headers {
                append("Content-Type", "application/json")
                append("Authorization", "Bearer $API_KEY")
            }
            setBody(
                AiRequestData(
                    model = MODEL_NAME,
                    max_tokens = 2048,
                    temperature = 0.3,
                    stream = false,
                    messages = listOf(
                        RequestMessage(COMMON_SYSTEM_PROMT, Role.SYSTEM.roleDescription),
                        RequestMessage(text, Role.USER.roleDescription)
                    )
                )
            )
        }.body<DeepSeekResult>()
    }


    /**
     * 发送流式请求并返回Flow处理数据块
     */
    suspend fun streamChatCompletion(text: String): Flow<ChatCompletionChunk> = callbackFlow {
        val response: HttpResponse = ktorClient.client.post("${BASE_URL}/chat/completions") {
            // 配置请求头
            headers {
                append("Content-Type", "application/json")
                append("Authorization", "Bearer $API_KEY")
            }
            setBody(
                AiRequestData(
                    model = MODEL_NAME,
                    max_tokens = 2048,
                    temperature = 0.3,
                    stream = true,
                    messages = listOf(
                        RequestMessage(COMMON_SYSTEM_PROMT, Role.SYSTEM.roleDescription),
                        RequestMessage(text, Role.USER.roleDescription)
                    )
                )
            )
        }

        val channel: ByteReadChannel = response.body()
        val buffer = StringBuilder()

        try {
            while (!channel.isClosedForRead) {
                val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                val text = packet.readText()

                if (text.isNotEmpty()) {
                    buffer.append(text)
                    processBuffer(buffer) { chunk ->
                        trySend(chunk)
                    }
                }
            }
            // 处理剩余数据
            processRemainingBuffer(buffer) { chunk ->
                trySend(chunk)
            }
        } catch (e: Exception) {
            // 处理读取异常
            if (e is CancellationException) throw e
            println("Stream reading error: ${e.message}")
            close(e)
        } finally {
            channel.cancel()
            close()
        }
    }

    /**
     * 处理缓冲区中的数据，解析SSE格式
     */
    private fun processBuffer(
        buffer: StringBuilder,
        onChunk: (ChatCompletionChunk) -> Unit
    ) {
        var index: Int
        while (buffer.indexOf("\n\n").also { index = it } != -1) {
            val eventData = buffer.substring(0, index)
            buffer.delete(0, index + 2)

            if (eventData.startsWith("data: ")) {
                val jsonData = eventData.removePrefix("data: ").trim()
                if (jsonData == "[DONE]") {
                    continue
                }
                try {
                    val chunk = Json.decodeFromString<ChatCompletionChunk>(jsonData)
                    onChunk(chunk)
                } catch (e: Exception) {
                    println("Failed to parse chunk: $jsonData, error: ${e.message}")
                }
            }
        }
    }

    /**
     * 处理剩余缓冲区数据
     */
    private fun processRemainingBuffer(
        buffer: StringBuilder,
        onChunk: (ChatCompletionChunk) -> Unit
    ) {
        val remaining = buffer.toString().trim()
        if (remaining.isNotEmpty() && remaining.startsWith("data: ") && remaining != "data: [DONE]") {
            val jsonData = remaining.removePrefix("data: ").trim()
            try {
                val chunk = Json.decodeFromString<ChatCompletionChunk>(jsonData)
                onChunk(chunk)
            } catch (e: Exception) {
                println("Failed to parse remaining chunk: $jsonData")
            }
        }
    }

    /**
     * 关闭客户端
     */
    fun close() {
        ktorClient.client.close()
    }
}