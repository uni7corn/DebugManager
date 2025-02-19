package com.stephen.debugmanager.data.uistate

data class AiModelState(
    val chatList: List<ChatItem> = listOf(),
    val listSize:Int = chatList.size
) {
    fun toUiState() = AiModelState(chatList = chatList, listSize = listSize)
}

data class ChatItem(
    val content: String,
    val isUser: Boolean,
)