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
import androidx.compose.material3.MaterialTheme
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
import com.stephen.debugmanager.data.AIModels
import com.stephen.debugmanager.data.ThemeState
import com.stephen.debugmanager.data.bean.Role
import com.stephen.debugmanager.net.DeepSeekRepository
import com.stephen.debugmanager.net.KimiRepository
import com.stephen.debugmanager.ui.component.*
import com.stephen.debugmanager.ui.theme.*
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.GlobalContext
import java.text.SimpleDateFormat

@Composable
fun AiModelPage() {
    BasePage("AI大模型对话") {

        val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

        val chatListState = mainStateHolder.aiModelChatListStateFlow.collectAsState()

        val aiModelOptions = mapOf(
            AIModels.DEEPSEEK to "DeepSeek",
            AIModels.KIMI to "Kimi"
        )

        var selectedModel = mainStateHolder.aiStoreStateFlow.collectAsState()

        val userInputSting = remember { mutableStateOf("") }

        val listState = rememberLazyListState()

        val themeState = mainStateHolder.themeStateStateFlow.collectAsState()

        val markDownColors = when (themeState.value) {
            ThemeState.DARK -> markDownDark
            ThemeState.LIGHT -> markDownLight
            else -> if (isSystemInDarkTheme()) markDownDark else markDownLight
        }

        LaunchedEffect(Unit) {
            // 获取上一次记忆的模型
            mainStateHolder.getStoredAiModel()
        }

        LaunchedEffect(chatListState.value.chatList.size) {
            // 每次列表更新，都滚动到最底部
            if (chatListState.value.chatList.isNotEmpty())
                listState.animateScrollToItem(chatListState.value.chatList.size - 1)
        }

        Column(modifier = Modifier.fillMaxSize(1f)) {

            DropdownSelector(
                aiModelOptions,
                selectedModel.value,
                modifier = Modifier.width(130.dp)
            ) {
                mainStateHolder.storeAiModel(it)
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(1f).weight(1f).padding(vertical = 10.dp),
                state = listState
            ) {
                items(chatListState.value.chatList) { chatItem ->
                    ChatItem(
                        content = chatItem.content,
                        role = chatItem.role,
                        modelName = when (chatItem.modelName) {
                            AIModels.DEEPSEEK -> DeepSeekRepository.MODEL_NAME
                            AIModels.KIMI -> KimiRepository.MODEL_NAME
                            else -> "Default"
                        },
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
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp).weight(1f),
                    onEnterPressed = {
                        mainStateHolder.chatWithAI(selectedModel.value, userInputSting.value)
                        userInputSting.value = ""
                    }
                )
                CommonButton(
                    "发送", onClick = {
                        mainStateHolder.chatWithAI(selectedModel.value, userInputSting.value)
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
    modelName: String,
    markDownColors: MarkdownColors
) {
    Box(modifier = Modifier.fillMaxWidth(1f)) {
        Row(
            modifier = Modifier.align(if (role == Role.USER) Alignment.CenterEnd else Alignment.CenterStart)
        ) {
            when (role) {
                Role.USER -> {
                    SelectionContainer(
                        modifier = Modifier.clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(10.dp)
                    ) {
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
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                        modifier = Modifier.padding(end = 8.dp).size(26.dp).clip(RoundedCornerShape(50))
                    )
                    Column {
                        Row(
                            modifier = Modifier.padding(bottom = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val ts = SimpleDateFormat("HH:mm").format(System.currentTimeMillis())

                            CenterText(
                                text = modelName,
                                style = infoText,
                                color = MaterialTheme.colorScheme.onSecondary,
                            )
                            CenterText(
                                text = ts,
                                style = infoText,
                                color = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier.padding(start = 5.dp)
                            )
                        }
                        // 大模型回复主内容以markdown格式展示
                        SelectionContainer {
                            Markdown(content = content, markDownColors, markdownTypography)
                        }
                    }

                }
            }
        }
    }
}