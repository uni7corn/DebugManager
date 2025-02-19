package com.stephen.debugmanager.data.bean

import kotlinx.serialization.Serializable


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

object Role {
    const val USER = "user"
    const val ASSISTANT = "assistant"
    const val SYSTEM = "system"
}