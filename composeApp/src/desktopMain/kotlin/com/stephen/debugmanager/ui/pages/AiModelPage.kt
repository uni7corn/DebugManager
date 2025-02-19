package com.stephen.debugmanager.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.model.MarkdownColors
import com.stephen.composeapp.generated.resources.Res
import com.stephen.composeapp.generated.resources.ic_robot
import com.stephen.debugmanager.MainStateHolder
import com.stephen.debugmanager.data.ThemeState
import com.stephen.debugmanager.data.bean.Role
import com.stephen.debugmanager.ui.component.BasePage
import com.stephen.debugmanager.ui.component.CenterText
import com.stephen.debugmanager.ui.component.CommonButton
import com.stephen.debugmanager.ui.component.WrappedEditText
import com.stephen.debugmanager.ui.theme.markDownDark
import com.stephen.debugmanager.ui.theme.markDownLight
import com.stephen.debugmanager.ui.theme.markdownDefaultText
import com.stephen.debugmanager.ui.theme.markdownTypography
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.GlobalContext

@Composable
fun AiModelPage() {
    BasePage("AI大模型对话") {

        val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

        val chatListState = mainStateHolder.aiModelChatListStateFlow.collectAsState()

        val userInputSting = remember { mutableStateOf("") }

        val listState = rememberLazyListState()

        val themeState = mainStateHolder.themeStateStateFlow.collectAsState()

        val markDownColors = when (themeState.value) {
            ThemeState.DARK -> markDownDark
            ThemeState.LIGHT -> markDownLight
            else -> if (isSystemInDarkTheme()) markDownLight else markDownDark
        }

        LaunchedEffect(chatListState.value.chatList.size) {
            if (chatListState.value.chatList.isNotEmpty())
                listState.animateScrollToItem(chatListState.value.chatList.size - 1)
        }

        Column(modifier = Modifier.fillMaxSize(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(1f).weight(1f).padding(vertical = 10.dp),
                state = listState
            ) {
                items(chatListState.value.chatList) { chatItem ->
                    ChatItem(
                        content = chatItem.content,
                        role = chatItem.role,
                        markDownColors = markDownColors
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(1f),
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
    role: Role,
    markDownColors: MarkdownColors
) {
    Box(modifier = Modifier.fillMaxWidth(1f)) {
        Row(
            modifier = Modifier.clip(RoundedCornerShape(10)).background(
                if (role == Role.USER) MaterialTheme.colors.surface else MaterialTheme.colors.background
            ).align(if (role == Role.USER) Alignment.CenterEnd else Alignment.CenterStart)
                .padding(vertical = 5.dp, horizontal = 10.dp)
        ) {
            // 大模型显示头像，并使用markdown组件来显示内容
            when (role) {
                Role.USER -> {
                    SelectionContainer {
                        CenterText(
                            text = content,
                            style = markdownDefaultText
                        )
                    }
                }

                else -> {
                    Image(
                        painter = painterResource(Res.drawable.ic_robot),
                        contentDescription = "logo",
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary),
                        modifier = Modifier.padding(end = 8.dp).size(24.dp).clip(RoundedCornerShape(50))
                    )
                    SelectionContainer {
                        Markdown(content = content, markDownColors, markdownTypography)
                    }
                }
            }
        }
    }
}