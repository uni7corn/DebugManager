package com.stephen.debugmanager.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.stephen.debugmanager.MainStateHolder
import com.stephen.debugmanager.ui.component.BasePage
import com.stephen.debugmanager.ui.component.CenterText
import com.stephen.debugmanager.ui.component.CommonButton
import com.stephen.debugmanager.ui.component.LocalFileChooser
import com.stephen.debugmanager.ui.component.WrappedEditText
import com.stephen.debugmanager.ui.component.rememberToastState
import com.stephen.debugmanager.ui.theme.groupTitleText
import org.koin.core.context.GlobalContext

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ToolsPage() {

    val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

    var logFolderPath by remember { mutableStateOf("") }

    val logTag = remember { mutableStateOf("") }

    val toastState = rememberToastState()

    BasePage("实用工具") {
        LazyColumn {
            item {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Column(
                        modifier = Modifier
                            .fillMaxRowHeight()
                            .padding(5.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(10.dp)
                    ) {
                        CenterText(
                            "日志文件处理",
                            style = groupTitleText,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        Column(modifier = Modifier.width(IntrinsicSize.Min)) {
                            LocalFileChooser(
                                tintText = "选择 日志 路径",
                                modifier = Modifier.fillMaxWidth(1f).padding(10.dp),
                                path = logFolderPath,
                                isChooseFile = false
                            ) {
                                logFolderPath = it
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth(1f).padding(10.dp)
                            ) {
                                WrappedEditText(
                                    value = logTag.value,
                                    tipText = "待寻找的tag(区分大小写)",
                                    onValueChange = {
                                        logTag.value = it
                                    },
                                    modifier = Modifier.padding(horizontal = 5.dp).weight(1f)
                                )
                                CommonButton(
                                    "开始处理", onClick = {
                                        if (logFolderPath.isEmpty()) {
                                            toastState.show("请先选择日志文件")
                                        } else if (logTag.value.isEmpty()) {
                                            toastState.show("请先输入待寻找的tag")
                                        } else {
                                            toastState.show("开始处理，完成后将自动打开所在文件夹")
                                            mainStateHolder.processLogFiles(
                                                logFolderPath,
                                                logTag.value
                                            )
                                        }
                                    },
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}