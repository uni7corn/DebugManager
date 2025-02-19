package com.stephen.debugmanager.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.stephen.composeapp.generated.resources.Res
import com.stephen.composeapp.generated.resources.app_logo
import com.stephen.debugmanager.MainStateHolder
import com.stephen.debugmanager.ui.component.BasePage
import com.stephen.debugmanager.ui.component.CenterText
import com.stephen.debugmanager.ui.component.CommonButton
import com.stephen.debugmanager.ui.component.WrappedEditText
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.GlobalContext

@Composable
fun AiModelPage() {
    BasePage("AI大模型对话") {

        val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

        val chatListState = mainStateHolder.aiModelChatListStateFlow.collectAsState()

        val userInputSting = remember { mutableStateOf("") }

        Box(modifier = Modifier.fillMaxSize(1f)) {
            LazyColumn(modifier = Modifier.fillMaxWidth(1f)) {
                items(chatListState.value.chatList) { chatItem ->
                    ChatItem(
                        content = chatItem.content,
                        isUser = chatItem.isUser
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(1f).align(Alignment.BottomCenter),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WrappedEditText(
                    value = userInputSting.value,
                    tipText = "输入对话文字",
                    onValueChange = { userInputSting.value = it },
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp).weight(1f)
                )
                CommonButton(
                    "发送", onClick = {
                        mainStateHolder.chatWithAI(userInputSting.value)
                        userInputSting.value = ""
                    },
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

    }
}

@Composable
fun ChatItem(
    content: String,
    isUser: Boolean,
) {
    Box(modifier = Modifier.fillMaxWidth(1f)) {
        Row(
            modifier = Modifier.clip(RoundedCornerShape(10)).background(
                if (isUser) MaterialTheme.colors.surface else MaterialTheme.colors.background
            ).align(if (isUser) Alignment.CenterEnd else Alignment.CenterStart)
                .padding(vertical = 5.dp, horizontal = 10.dp)
        ) {
            // 大模型显示头像
            if (!isUser)
                Image(
                    painter = painterResource(Res.drawable.app_logo),
                    contentDescription = "logo",
                    modifier = Modifier.padding(end = 8.dp).size(24.dp).clip(RoundedCornerShape(50))
                )
            CenterText(text = content)
        }

    }

}
