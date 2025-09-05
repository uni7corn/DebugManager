package com.stephen.debugmanager.ui.pages

import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.stephen.composeapp.generated.resources.*
import com.stephen.debugmanager.MainStateHolder
import com.stephen.debugmanager.base.PlatformAdapter
import com.stephen.debugmanager.data.uistate.DirectoryState
import com.stephen.debugmanager.helper.FileManager
import com.stephen.debugmanager.ui.component.*
import com.stephen.debugmanager.ui.theme.infoText
import com.stephen.debugmanager.ui.theme.itemKeyText
import com.stephen.debugmanager.utils.DoubleClickUtils
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.context.GlobalContext

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun FileManagePage(
    directoryState: DirectoryState,
    isDeviceConnected: Boolean,
    destinationCall: (des: String) -> Unit
) {

    val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

    val toastState = rememberToastState()

    val deleteConfirmDialogState = remember { mutableStateOf(false) }
    val pushFileConfirmDialogState = remember { mutableStateOf(false) }
    val pushFolderConfirmDialogState = remember { mutableStateOf(false) }

    var desktopSelectedFolderPath by remember { mutableStateOf("") }

    var desktopSelectedFile by remember { mutableStateOf("") }

    var androidSelectedFile by remember { mutableStateOf("") }

    val rootTip = stringResource(Res.string.file_manage_page_root_tip)
    val chooseFirstTip = stringResource(Res.string.file_manage_page_choose_first_tip)
    val copyTip = stringResource(Res.string.file_manage_page_copied_tip)

    BasePage({
        Box {
            Column {
                // 功能按钮，删除，刷新等等
                Row(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth(1f)
                        .clip(RoundedCornerShape(10))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_last_directory),
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(28.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                // 判断是否是根目录
                                if (directoryState.currentdirectory == FileManager.ROOT_DIR) {
                                    toastState.show(rootTip)
                                } else {
                                    destinationCall(FileManager.LAST_FOLDER)
                                }
                            },
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
                        contentDescription = "ic_last_directory"
                    )
                    Image(
                        painter = painterResource(Res.drawable.ic_delete),
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(28.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (androidSelectedFile.isEmpty()) {
                                    toastState.show(chooseFirstTip)
                                } else {
                                    deleteConfirmDialogState.value = true
                                }
                            },
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.error),
                        contentDescription = "ic_delete"
                    )
                    CommonButton(
                        "sdcard",
                        onClick = {
                            destinationCall(FileManager.SD_CARD)
                        },
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    CommonButton(
                        "priv-app",
                        onClick = {
                            destinationCall(FileManager.PRIV_APP)
                        }
                    )
                }

                // 目录列表
                FileDragArea(
                    modifier = Modifier.fillMaxWidth(1f).weight(1f),
                    onSelectFile = {
                        desktopSelectedFile = it
                        pushFileConfirmDialogState.value = true
                    },
                    onSelectFolder = {
                        desktopSelectedFolderPath = it
                        pushFolderConfirmDialogState.value = true
                    }
                ) {
                    val refreshString = stringResource(Res.string.device_page_refresh)
                    // 文件列表
                    ContextMenuArea(items = {
                        listOf(
                            ContextMenuItem(refreshString) {
                                androidSelectedFile = ""
                                directoryState.currentdirectory.let { mainStateHolder.updateFileList(it) }
                            }
                        )
                    }) {
                        Column(
                            modifier = Modifier.fillMaxSize(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    // 清除选中文件
                                    androidSelectedFile = ""
                                }
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            CenterText(
                                "${directoryState.deviceCode}:${directoryState.currentdirectory}",
                                style = itemKeyText,
                                modifier = Modifier.padding(start = 10.dp, top = 10.dp, bottom = 10.dp),
                            )
                            LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 105.dp)) {
                                items(directoryState.subdirectories.filter { it.fileName.isNotEmpty() }
                                    .sortedBy { it.fileName }, key = { it.fileName }) {
                                    Box(
                                        Modifier.animateItem(
                                            fadeInSpec = null,
                                            fadeOutSpec = null,
                                            placementSpec = tween(300)
                                        ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        FileViewItem(
                                            it.fileName,
                                            it.isDirectory,
                                            modifier = Modifier
                                                .animateItem(
                                                    fadeInSpec = null,
                                                    fadeOutSpec = null,
                                                    placementSpec = tween(300)
                                                )
                                                .size(100.dp)
                                                .clip(RoundedCornerShape(10))
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) {
                                                    // 点击则设置即将操作的path
                                                    androidSelectedFile = it.fileName
                                                    // 双击，执行操作
                                                    if (DoubleClickUtils.isFastDoubleClick()) {
                                                        if (it.isDirectory)
                                                            destinationCall(it.fileName)
                                                    }
                                                }.border(
                                                    1.dp,
                                                    if (androidSelectedFile.split("/")
                                                            .last() == it.fileName
                                                    ) MaterialTheme.colorScheme.onPrimary else Color.Transparent,
                                                    RoundedCornerShape(10)
                                                ).background(
                                                    // android端分隔符固定为/
                                                    if (androidSelectedFile.split("/")
                                                            .last() == it.fileName
                                                    ) MaterialTheme.colorScheme.onSurface else Color.Transparent
                                                ),
                                            onClickDelete = { name ->
                                                androidSelectedFile = name
                                                deleteConfirmDialogState.value = true
                                            },
                                            onClickMove = { name ->
                                                androidSelectedFile = name
                                            },
                                            onClickCopy = { name ->
                                                androidSelectedFile = name
                                            },
                                            onClickPull = { name ->
                                                androidSelectedFile = name
                                                mainStateHolder.pullFileFromAndroid(androidSelectedFile)
                                            },
                                            onClickCopyPath = { name ->
                                                androidSelectedFile = name
                                                val path = directoryState.currentdirectory + "/" + androidSelectedFile
                                                mainStateHolder.copyPathToClipboard(path)
                                                toastState.show(copyTip)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                CenterText(
                    stringResource(Res.string.file_manage_page_drag_tip),
                    style = infoText,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.fillMaxWidth(1f)
                )
            }
            // 设备未连接提示
            if (isDeviceConnected.not()) {
                DeviceNoneConnectShade()
            }
            // 删除确认弹窗
            if (deleteConfirmDialogState.value)
                CommonDialog(
                    title = stringResource(Res.string.file_manage_page_delete_confirm, androidSelectedFile),
                    onConfirm = {
                        deleteConfirmDialogState.value = false
                        mainStateHolder.deleteFileOrFolder(androidSelectedFile)
                        directoryState.currentdirectory.let { mainStateHolder.updateFileList(it) }
                    },
                    onCancel = { deleteConfirmDialogState.value = false },
                    onDismiss = { deleteConfirmDialogState.value = false }
                )
            // 推送文件确认弹窗
            if (pushFileConfirmDialogState.value)
                CommonDialog(
                    title = "推送文件 ${
                        desktopSelectedFile.split(PlatformAdapter.sp).last()
                    } 到 ${directoryState.currentdirectory}？",
                    onConfirm = {
                        pushFileConfirmDialogState.value = false
                        mainStateHolder.pushFileToAndroid(desktopSelectedFile, directoryState.currentdirectory)
                        directoryState.currentdirectory.let { mainStateHolder.updateFileList(it) }
                    },
                    onCancel = { pushFileConfirmDialogState.value = false },
                    onDismiss = { pushFileConfirmDialogState.value = false }
                )
            if (pushFolderConfirmDialogState.value)
                CommonDialog(
                    title = "推送文件夹 ${
                        desktopSelectedFolderPath.split(PlatformAdapter.sp).last()
                    } 到 ${directoryState.currentdirectory}？",
                    onConfirm = {
                        pushFolderConfirmDialogState.value = false
                        mainStateHolder.pushFolderToAndroid(desktopSelectedFolderPath, directoryState.currentdirectory)
                        directoryState.currentdirectory.let { mainStateHolder.updateFileList(it) }
                    },
                    onCancel = { pushFolderConfirmDialogState.value = false },
                    onDismiss = { pushFolderConfirmDialogState.value = false }
                )
        }
    })
}

@Composable
fun FileViewItem(
    name: String,
    isDirectory: Boolean,
    modifier: Modifier,
    onClickDelete: (name: String) -> Unit = {},
    onClickMove: (name: String) -> Unit = {},
    onClickCopy: (name: String) -> Unit = {},
    onClickPull: (name: String) -> Unit = {},
    onClickCopyPath: (name: String) -> Unit = {},
) {
    val deleteString = stringResource(Res.string.file_manage_page_delete)
    val moveString = stringResource(Res.string.file_manage_page_move)
    val copyString = stringResource(Res.string.file_manage_page_copy)
    val pullString = stringResource(Res.string.file_manage_page_pull)
    val copyPathString = stringResource(Res.string.file_manage_page_copy_path)
    ContextMenuArea(items = {
        listOf(
            ContextMenuItem(deleteString) {
                onClickDelete(name)
            },
            ContextMenuItem(moveString) {
                onClickMove(name)
            },
            ContextMenuItem(copyString) {
                onClickCopy(name)
            },
            ContextMenuItem(pullString) {
                onClickPull(name)
            },
            ContextMenuItem(copyPathString) {
                onClickCopyPath(name)
            },
        )
    }) {
        Box(
            modifier = modifier.padding(5.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = if (isDirectory) painterResource(Res.drawable.ic_folder)
                    else painterResource(Res.drawable.ic_file),
                    modifier = Modifier.size(36.dp),
                    contentDescription = "file_icon"
                )
                CenterText(
                    name,
                    modifier = Modifier.padding(6.dp),
                    isNeedToClipText = true,
                    style = infoText,
                )
            }
        }
    }
}