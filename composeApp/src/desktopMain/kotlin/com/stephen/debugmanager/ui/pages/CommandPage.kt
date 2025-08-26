package com.stephen.debugmanager.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.stephen.debugmanager.MainStateHolder
import com.stephen.debugmanager.data.bean.CommandData
import com.stephen.debugmanager.data.bean.CommandType
import com.stephen.debugmanager.ui.component.*
import com.stephen.debugmanager.ui.theme.groupTitleText
import com.stephen.debugmanager.ui.theme.infoText
import org.koin.core.context.GlobalContext

@Composable
fun CommandPage(isDeviceConnected: Boolean) {

    val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

    val toastState = rememberToastState()

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    var androidShellCommand by remember { mutableStateOf("") }
    var terminalCommand by remember { mutableStateOf("") }

    val terminalExecuteList = mainStateHolder.terminalExecuteListState.value
    val androidExecuteList = mainStateHolder.androidShellExecuteListState.value

    val terminalListScrollState = rememberLazyListState()
    val androidListScrollState = rememberLazyListState()

    LaunchedEffect(terminalExecuteList.size) {
        if (terminalExecuteList.isNotEmpty()) {
            terminalListScrollState.animateScrollToItem(terminalExecuteList.size - 1)
        }
    }

    LaunchedEffect(androidExecuteList.size) {
        if (androidExecuteList.isNotEmpty()) {
            androidListScrollState.animateScrollToItem(androidExecuteList.size - 1)
        }
    }

    BasePage("命令模式") {
        Box {
            Row {
                Column(
                    modifier = Modifier.padding(end = 10.dp).fillMaxHeight(1f).weight(0.5f)
                        .clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.surface)
                        .padding(10.dp)
                ) {
                    CenterText(
                        "Terminal",
                        modifier = Modifier.padding(bottom = 10.dp),
                        style = groupTitleText
                    )

                    // 命令执行窗口 Composable
                    CommandExecuteWindow(
                        modifier = Modifier.padding(bottom = 10.dp)
                            .fillMaxWidth(1f).weight(1f),
                        scrollState = terminalListScrollState,
                        executeList = terminalExecuteList
                    )


                    Row(verticalAlignment = Alignment.CenterVertically) {
                        WrappedEditText(
                            value = terminalCommand,
                            tipText = "输入Terminal命令",
                            onValueChange = { terminalCommand = it },
                            modifier = Modifier
                                .padding(start = 10.dp, end = 10.dp).weight(1f)
                                .focusRequester(focusRequester),
                            onEnterPressed = {
                                if (terminalCommand.isEmpty()) {
                                    toastState.show("请输入命令")
                                } else {
                                    mainStateHolder.executeTerminalCommand(terminalCommand)
                                    terminalCommand = ""
                                }
                            }
                        )
                        CommonButton(
                            "执行", onClick = {
                                if (terminalCommand.isEmpty()) {
                                    toastState.show("请输入命令")
                                } else {
                                    mainStateHolder.executeTerminalCommand(terminalCommand)
                                    terminalCommand = ""
                                }
                            },
                            modifier = Modifier.padding(10.dp),
                            btnColor = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxHeight(1f).weight(0.5f)
                        .clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.surface)
                        .padding(10.dp)
                ) {
                    CenterText(
                        "Android Shell",
                        modifier = Modifier.padding(bottom = 10.dp),
                        style = groupTitleText
                    )

                    // 命令执行窗口 Composable
                    CommandExecuteWindow(
                        modifier = Modifier.padding(bottom = 10.dp)
                            .fillMaxWidth(1f).weight(1f),
                        scrollState = androidListScrollState,
                        executeList = androidExecuteList
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        WrappedEditText(
                            value = androidShellCommand,
                            tipText = "输入adb命令",
                            onValueChange = { androidShellCommand = it },
                            modifier = Modifier
                                .padding(start = 10.dp, end = 10.dp).weight(1f)
                                .focusRequester(focusRequester),
                            onEnterPressed = {
                                if (androidShellCommand.isEmpty()) {
                                    toastState.show("请输入命令")
                                } else {
                                    mainStateHolder.executeAndroidShellCommand(androidShellCommand)
                                    androidShellCommand = ""
                                }
                            }
                        )
                        CommonButton(
                            "执行", onClick = {
                                if (androidShellCommand.isEmpty()) {
                                    toastState.show("请输入命令")
                                } else {
                                    mainStateHolder.executeAndroidShellCommand(androidShellCommand)
                                    androidShellCommand = ""
                                }
                            },
                            modifier = Modifier.padding(10.dp),
                            btnColor = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
            if (isDeviceConnected.not()) {
                focusManager.clearFocus()
                DeviceNoneConnectShade()
            }
        }
    }
}

@Composable
fun CommandExecuteWindow(
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState(),
    executeList: List<CommandData>
) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(10.dp))
            .background(Color.Black)
    ) {
        SelectionContainer {
            LazyColumn(state = scrollState) {
                items(executeList) {
                    when (it.type) {
                        CommandType.USER -> {
                            CenterText(
                                it.contents,
                                modifier = Modifier.fillParentMaxWidth(1f),
                                color = Color.Green,
                                alignment = Alignment.TopStart
                            )
                        }

                        CommandType.SYSTEM -> {
                            CenterText(
                                it.contents,
                                modifier = Modifier.fillParentMaxWidth(1f),
                                style = infoText,
                                color = Color.White,
                                alignment = Alignment.TopStart
                            )
                        }
                    }
                }
            }
        }
    }
}