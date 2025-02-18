package com.stephen.debugmanager.data.bean

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class KimiResult(
    val choices: List<Choice>,
    val created: Int,
    val id: String,
    val model: String,
    val `object`: String,
    val usage: Usage
)

@Serializable
data class Choice(
    val finish_reason: String,
    val index: Int,
    val message: ResultMessage
)

@Serializable
data class Usage(
    val completion_tokens: Int,
    val prompt_tokens: Int,
    val total_tokens: Int
)

@Serializable
data class ResultMessage(
    val content: String,
    val role: String
)