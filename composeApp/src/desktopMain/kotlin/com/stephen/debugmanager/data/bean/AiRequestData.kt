package com.stephen.debugmanager.data.bean

import kotlinx.serialization.Serializable


/**
 * 各个模型应该为通用参数
 */
@Serializable
data class AiRequestData(
    val max_tokens: Int,
    val messages: List<RequestMessage>,
    val model: String,
    val stream: Boolean,
    val temperature: Double
)

@Serializable
data class RequestMessage(
    val content: String,
    val role: String
)

enum class Role(val roleDescription: String) {
    USER("user"),
    ASSISTANT("assistant"),
    SYSTEM("system")
}