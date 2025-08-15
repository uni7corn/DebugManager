package com.stephen.debugmanager.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
                ) {
                    CommonButton(
                        onClick = { destinationCall(FileManager.LAST_FOLDER) },
                        text = "${Constants.LEFT_ARROW}返回上一级"
                    )
                    CommonButton(
                        onClick = { destinationCall(FileManager.ROOT_DIR) }, text = "回到根目录",
                        modifier = Modifier.padding(start = 10.dp)
                    )
                    CommonButton(
                        onClick = { destinationCall(FileManager.ROOT_DIR) }, text = "去向sdcard",
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
                        modifier = Modifier.fillMaxHeight(1f).padding(end = 10.dp).weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        CenterText(
                            "${directoryState.deviceCode}:${directoryState.currentdirectory}",
                            style = itemKeyText,
                            modifier = Modifier.padding(start = 10.dp, top = 10.dp, bottom = 10.dp),
                        )
                        // 文件列表
                        LazyVerticalGrid(columns = GridCells.Fixed(5)) {
                            items(directoryState.subdirectories.sortedBy { it.fileName }) {
                                if (it.fileName.isNotEmpty())
                                    FileViewItem(
                                        it.fileName,
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
                                        }.background(
                                            // android端分隔符固定为/
                                            if (androidSelectedFile.split("/")
                                                    .last() == it.path
                                            ) MaterialTheme.colorScheme.onSurface else Color.Transparent
                                        )
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
fun FileViewItem(name: String, isDirectory: Boolean, modifier: Modifier) {
    Column(
        modifier = modifier.padding(5.dp).width(IntrinsicSize.Max),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = if (isDirectory) painterResource(Res.drawable.ic_folder)
            else painterResource(Res.drawable.ic_file),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
            modifier = Modifier.size(36.dp),
            contentDescription = "file_icon"
        )
        CenterText(
            name,
            modifier = Modifier.padding(6.dp),
            style = defaultText,
        )
    }
}
