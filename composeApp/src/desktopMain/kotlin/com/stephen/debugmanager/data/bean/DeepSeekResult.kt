package com.stephen.debugmanager.data.bean

import kotlinx.serialization.Serializable

@Serializable
data class DeepSeekResult(
    val choices: List<Choice>,
    val created: Int,
    val id: String,
    val model: String,
    val `object`: String,
    val system_fingerprint: String,
    val usage: Usage
)

@Serializable
data class Choice(
    val finish_reason: String,
    val index: Int,
    val logprobs: String?,
    val message: Message
)

@Serializable
data class Usage(
    val completion_tokens: Int,
    val prompt_cache_hit_tokens: Int,
    val prompt_cache_miss_tokens: Int,
    val prompt_tokens: Int,
    val prompt_tokens_details: PromptTokensDetails,
    val total_tokens: Int
)

@Serializable
data class Message(
    val content: String,
    val role: String? = ""
)

@Serializable
data class PromptTokensDetails(
    val cached_tokens: Int
)