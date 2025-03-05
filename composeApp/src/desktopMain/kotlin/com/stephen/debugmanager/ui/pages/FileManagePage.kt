package com.stephen.debugmanager.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.stephen.composeapp.generated.resources.Res
import com.stephen.composeapp.generated.resources.ic_file
import com.stephen.composeapp.generated.resources.ic_folder
import com.stephen.debugmanager.MainStateHolder
import com.stephen.debugmanager.base.PlatformAdapter
import com.stephen.debugmanager.data.Constants
import com.stephen.debugmanager.data.Constants.PULL_FILE_TOAST
import com.stephen.debugmanager.data.FileOperationType
import com.stephen.debugmanager.data.uistate.DirectoryState
import com.stephen.debugmanager.helper.FileManager
import com.stephen.debugmanager.ui.component.*
import com.stephen.debugmanager.ui.theme.defaultText
import com.stephen.debugmanager.ui.theme.groupTitleText
import com.stephen.debugmanager.ui.theme.itemKeyText
import com.stephen.debugmanager.utils.DoubleClickUtils
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.GlobalContext

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FileManagePage(
    directoryState: DirectoryState,
    isDeviceConnected: Boolean,
    destinationCall: (des: String) -> Unit
) {

    val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

    val toastState = rememberToastState()

    val deleteConfirmDialogState = remember { mutableStateOf(false) }

    var desktopSelectedFolderPath by remember { mutableStateOf("") }

    var desktopSelectedFile by remember { mutableStateOf("") }

    var androidSelectedFile by remember { mutableStateOf("") }

    BasePage("文件管理器") {
        Box {
            Column {
                FlowRow(
                    verticalArrangement = Arrangement.Center,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    CommonButton(
                        onClick = { destinationCall(FileManager.LAST_DIR) },
                        text = "${Constants.LEFT_ARROW}返回上一级"
                    )
                    CommonButton(
                        onClick = { destinationCall(FileManager.ROOT_DIR) }, text = "回到根目录",
                        modifier = Modifier.padding(start = 10.dp)
                    )
                    CommonButton(
                        onClick = { destinationCall(FileManager.SDCARD_DIR) }, text = "去向sdcard",
                        modifier = Modifier.padding(start = 10.dp)
                    )
                    CommonButton(
                        onClick = { destinationCall(FileManager.PRIV_APP) }, text = "去向priv-app",
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
                Row {
                    CenterText(
                        "当前操作中的文件：${androidSelectedFile}", style = defaultText,
                        modifier = Modifier.padding(bottom = 10.dp).fillMaxWidth(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(10.dp)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(1f)) {
                    Column(
                        modifier = Modifier.fillMaxHeight(1f).padding(end = 10.dp).weight(0.6f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        CenterText(
                            "${directoryState.deviceCode}:${directoryState.currentdirectory}",
                            style = itemKeyText,
                            modifier = Modifier.padding(start = 10.dp, top = 10.dp, bottom = 10.dp),
                        )
                        // 文件列表
                        LazyColumn {
                            items(directoryState.subdirectories.sortedBy { it.path }) {
                                if (it.path != ".." && it.path != ".")
                                    FileViewItem(
                                        it.path,
                                        it.isDirectory,
                                        modifier = Modifier.clickable {
                                            // 点击则设置即将操作的path
                                            mainStateHolder.setSelectedFilePath(it.path)
                                            androidSelectedFile = mainStateHolder.getSelectedPath()
                                            // 双击，执行操作
                                            if (DoubleClickUtils.isFastDoubleClick()) {
                                                if (it.isDirectory)
                                                    destinationCall(it.path)
                                            }
                                        }.fillParentMaxWidth().background(
                                            // android端分隔符固定为/
                                            if (androidSelectedFile.split("/")
                                                    .last() == it.path
                                            ) MaterialTheme.colorScheme.onSurface else Color.Transparent
                                        )
                                    )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.weight(0.4f).clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surface).padding(10.dp)
                    ) {
                        item {
                            Column(
                                modifier = Modifier.fillParentMaxWidth(1f)
                                    .padding(bottom = 10.dp)
                            ) {
                                CenterText(
                                    "Android内操作",
                                    modifier = Modifier.padding(bottom = 10.dp),
                                    style = groupTitleText
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CommonButton(
                                        text = "复制", onClick = {
                                            toastState.show(
                                                "选中文件:${mainStateHolder.getSelectedPath()}\n" +
                                                        "在左侧区域选择目标文件夹，然后点击粘贴"
                                            )
                                            directoryState.currentdirectory?.let {
                                                mainStateHolder.setFileOperationState(
                                                    FileOperationType.COPY
                                                )
                                                destinationCall("/")
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    CommonButton(
                                        text = "剪切", onClick = {
                                            toastState.show(
                                                "选中文件:${mainStateHolder.getSelectedPath()}\n" +
                                                        "在左侧区域选择目标文件夹，然后点击粘贴"
                                            )
                                            directoryState.currentdirectory?.let {
                                                mainStateHolder.setFileOperationState(
                                                    FileOperationType.MOVE
                                                )
                                                destinationCall("/")
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    CommonButton(
                                        text = "粘贴", onClick = {
                                            toastState.show("粘贴成功")
                                            // 粘贴文件，刷新
                                            directoryState.currentdirectory?.let {
                                                mainStateHolder.pasteFileOrFolder()
                                                mainStateHolder.updateFileList(it)
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 10.dp)
                                ) {
                                    CommonButton(
                                        text = "删除", onClick = {
                                            deleteConfirmDialogState.value = true
                                        },
                                        modifier = Modifier.weight(1f),
                                        btnColor = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            SimpleDivider(
                                modifier = Modifier.fillParentMaxWidth(1f)
                                    .padding(bottom = 10.dp, end = 10.dp, start = 10.dp)
                                    .height(1.dp)
                            )
                        }
                        item {
                            Column(
                                modifier = Modifier.fillParentMaxWidth(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surface).padding(10.dp)
                            ) {
                                CenterText(
                                    "推送Desktop单文件",
                                    modifier = Modifier.padding(bottom = 10.dp),
                                    style = groupTitleText
                                )

                                FileChooseWidget(
                                    tintText = "将文件拖到此处 或 点击选取",
                                    path = desktopSelectedFile,
                                    isChooseFile = true,
                                    modifier = Modifier.fillMaxWidth(1f).padding(bottom = 10.dp)
                                ) {
                                    desktopSelectedFile = it
                                }
                                CenterText(
                                    text = "待接收的Android路径: ${directoryState.currentdirectory}",
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )

                                CommonButton(
                                    text = "PUSH",
                                    modifier = Modifier.padding(bottom = 10.dp),
                                    onClick = {
                                        if (desktopSelectedFile.isNotEmpty()) {
                                            toastState.show("开始推送文件，请勿多次点击")
                                            mainStateHolder.pushFileToAndroid(
                                                desktopSelectedFile,
                                                "${directoryState.currentdirectory}/${
                                                    desktopSelectedFile.split(PlatformAdapter.sp)
                                                        .last()
                                                }"
                                            )
                                            directoryState.currentdirectory?.let { it1 ->
                                                mainStateHolder.updateFileList(it1)
                                            }
                                        } else {
                                            toastState.show("请选择电脑端要推送到Android的文件")
                                        }
                                    }
                                )

                                SimpleDivider(
                                    modifier = Modifier.fillParentMaxWidth(1f)
                                        .padding(bottom = 10.dp, end = 10.dp, start = 10.dp)
                                        .height(1.dp)
                                )

                                CenterText(
                                    "推送Desktop的文件夹",
                                    modifier = Modifier.padding(bottom = 10.dp),
                                    style = groupTitleText
                                )

                                FileChooseWidget(
                                    tintText = "将文件夹拖到此处 或 点击选取",
                                    path = desktopSelectedFolderPath,
                                    isChooseFile = false,
                                    modifier = Modifier.fillMaxWidth(1f).padding(bottom = 10.dp),
                                ) {
                                    desktopSelectedFolderPath = it
                                }
                                CenterText(
                                    text = "待接收的Android路径: ${directoryState.currentdirectory}",
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )

                                CommonButton(
                                    text = "PUSH",
                                    modifier = Modifier.padding(bottom = 10.dp),
                                    onClick = {
                                        if (desktopSelectedFolderPath.isNotEmpty()) {
                                            toastState.show("开始推送文件夹，请勿多次点击")
                                            mainStateHolder.pushFolderToAndroid(
                                                desktopSelectedFolderPath,
                                                "${directoryState.currentdirectory}/${
                                                    desktopSelectedFolderPath.split(
                                                        PlatformAdapter.sp
                                                    ).last()
                                                }"
                                            )
                                        } else {
                                            toastState.show("请选择电脑端要推送到Android的文件夹")
                                        }
                                    }
                                )
                                SimpleDivider(
                                    modifier = Modifier.fillParentMaxWidth(1f)
                                        .padding(bottom = 10.dp)
                                        .height(1.dp)
                                )
                                // pull界面
                                CenterText(
                                    "从Android拉取文件",
                                    modifier = Modifier.padding(bottom = 10.dp),
                                    style = groupTitleText
                                )

                                CenterText(
                                    text = "待拉取的文件: $androidSelectedFile",
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )

                                CenterText(
                                    text = "默认pull到: ${PlatformAdapter.desktopTempFolder}",
                                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                                        .padding(bottom = 10.dp)
                                        .background(MaterialTheme.colorScheme.secondary)
                                        .border(
                                            2.dp,
                                            MaterialTheme.colorScheme.onSecondary,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .padding(10.dp)
                                )

                                CommonButton(
                                    text = "PULL",
                                    modifier = Modifier.padding(bottom = 10.dp),
                                    onClick = {
                                        mainStateHolder.pullFileFromAndroid(androidSelectedFile)
                                        toastState.show(PULL_FILE_TOAST)
                                    }
                                )

                            }
                        }
                    }
                }
            }
            // 设备未连接提示
            if (isDeviceConnected.not()) {
                DeviceNoneConnectShade()
            }

            // 删除确认弹窗
            if (deleteConfirmDialogState.value)
                CommonDialog(
                    title = "确认删除${androidSelectedFile}？",
                    onConfirm = {
                        deleteConfirmDialogState.value = false
                        mainStateHolder.deleteFileOrFolder(mainStateHolder.getSelectedPath())
                        directoryState.currentdirectory?.let { mainStateHolder.updateFileList(it) }
                    },
                    onCancel = { deleteConfirmDialogState.value = false },
                    onDismiss = { deleteConfirmDialogState.value = false }
                )
        }
    }
}

@Composable
fun FileViewItem(path: String, isDirectory: Boolean, modifier: Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(start = 20.dp)
    ) {
        Image(
            painter = if (isDirectory) painterResource(Res.drawable.ic_folder)
            else painterResource(Res.drawable.ic_file),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
            modifier = Modifier.size(15.dp),
            contentDescription = "file_icon"
        )
        CenterText(
            path,
            modifier = Modifier.padding(6.dp),
            style = defaultText,
        )
    }
}
