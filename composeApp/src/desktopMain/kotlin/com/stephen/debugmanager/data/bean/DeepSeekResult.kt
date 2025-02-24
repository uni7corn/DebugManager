package com.stephen.debugmanager.data.bean

import kotlinx.serialization.Serializable

@Serializable
data class DeepSeekResult(
    val choices: List<Choice>,
    val created: Int,
    val id: String,
    val model: String,
    val `object`: String,
    val prompt_logprobs: String?,
    val usage: Usage
)

@Serializable
data class Choice(
    val finish_reason: String,
    val index: Int,
    val logprobs: String?,
    val message: Message,
    val stop_reason: String?
)

@Serializable
data class Usage(
    val completion_tokens: Int,
    val prompt_tokens: Int,
    val total_tokens: Int
)

@Serializable
data class Message(
    val content: String,
    val reasoning_content: String?,
    val role: String,
    val tool_calls: List<String?>
)