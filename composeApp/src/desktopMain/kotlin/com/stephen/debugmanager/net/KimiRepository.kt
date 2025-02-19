package com.stephen.debugmanager.net

import com.stephen.debugmanager.data.bean.AiRequestData
import com.stephen.debugmanager.data.bean.KimiResult
import com.stephen.debugmanager.data.bean.RequestMessage
import com.stephen.debugmanager.data.bean.Role
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KimiRepository(private val ktorClient: KtorClient) {

    companion object {
        const val BASE_URL = "https://api.moonshot.cn/v1/chat/completions"
        const val COMMON_SYSTEM_PROMT = "你是一个人工智能系统，可以根据用户的输入来返回生成式的回复"
        const val API_KEY = "sk-XXXXXXXXXXXXXXXXXXXXXX"
    }

    suspend fun chatWithMoonShotKimi(text: String) = withContext(Dispatchers.IO) {
        ktorClient.client.post(BASE_URL) {
            // 配置请求头
            headers {
                append("Content-Type", "application/json")
                append("Authorization", "Bearer $API_KEY")
            }
            setBody(
                AiRequestData(
                    model = "moonshot-v1-32k-vision-preview",
                    max_tokens = 2048,
                    temperature = 0.3,
                    stream = false,
                    messages = listOf(
                        RequestMessage(COMMON_SYSTEM_PROMT, Role.SYSTEM.roleDescription),
                        RequestMessage(text, Role.USER.roleDescription)
                    )
                )
            )
        }.body<KimiResult>()
    }

    fun test() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = chatWithMoonShotKimi("你好")
            println(result)
        }
    }
}