package com.stephen.debugmanager.net

import com.stephen.debugmanager.data.bean.AiRequestData
import com.stephen.debugmanager.data.bean.DeepSeekResult
import com.stephen.debugmanager.data.bean.RequestMessage
import com.stephen.debugmanager.data.bean.Role
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeepSeekRepository(private val ktorClient: KtorClient) {

    companion object {
        const val BASE_URL =
            "https://api.deepseek.com"
        const val COMMON_SYSTEM_PROMT = "你是一个人工智能系统，可以根据用户的输入来返回生成式的回复"
        // 改为vong
        val API_KEY: String = System.getenv("DEEPSEEK_API_KEY") ?: ""
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
}