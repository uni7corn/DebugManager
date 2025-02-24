package com.stephen.debugmanager.data.uistate

import com.stephen.debugmanager.data.bean.Role

data class AiModelState(
    val chatList: List<ChatItem> = listOf(),
    val listSize: Int = chatList.size
) {
    fun toUiState() = AiModelState(chatList = chatList, listSize = listSize)
}

data class ChatItem(
    val content: String,
    val modelName: Int = 0,
    val role: Role,
)