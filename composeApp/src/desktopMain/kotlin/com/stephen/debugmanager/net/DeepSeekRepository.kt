package com.stephen.debugmanager.net

import com.stephen.debugmanager.data.bean.AiRequestData
import com.stephen.debugmanager.data.bean.DeepSeekResult
import com.stephen.debugmanager.data.bean.RequestMessage
import com.stephen.debugmanager.data.bean.Role
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.PrintStream

class DeepSeekRepository(private val ktorClient: KtorClient) {

    companion object {
        const val BASE_URL =
            "/v1/chat/completions"
        const val COMMON_SYSTEM_PROMT = "你是一个人工智能系统，可以根据用户的输入来返回生成式的回复"
        const val API_KEY = "xxxxxxxxxxx"
        const val MODEL_NAME = "DeepSeek-V3"
    }

    suspend fun chatWithDeepSeek(text: String) = withContext(Dispatchers.IO) {
        ktorClient.client.post(BASE_URL) {
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

    fun test() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = chatWithDeepSeek("你好")
            PrintStream(System.out, true, "UTF-8").println(result)
        }
    }
}