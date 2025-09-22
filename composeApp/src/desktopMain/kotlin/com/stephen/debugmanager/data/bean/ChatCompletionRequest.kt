package com.stephen.debugmanager.data.bean

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletionChunk(
    val choices: List<StreamChoice>,
    val created: Int,
    val id: String,
    val model: String,
    @SerialName("object")
    val `object`: String,
    val system_fingerprint: String
)

@Serializable
data class StreamChoice(
    val delta: Delta,
    val finish_reason: String?,
    val index: Int,
    val logprobs: String?
)

@Serializable
data class Delta(
    val content: String,
)