package com.stephen.debugmanager.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import com.stephen.debugmanager.helper.FileManager
import com.stephen.debugmanager.data.uistate.DirectoryState
import com.stephen.debugmanager.ui.component.*
import com.stephen.debugmanager.ui.theme.defaultText
import com.stephen.debugmanager.ui.theme.groupTitleText
import com.stephen.debugmanager.ui.theme.infoText
import com.stephen.debugmanager.ui.theme.itemKeyText
import com.stephen.debugmanager.utils.DoubleClickUtils
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.GlobalContext
import java.awt.FileDialog
import java.awt.Frame
import javax.swing.JFileChooser

@Composable
fun FileManagePage(directoryState: DirectoryState, destinationCall: (des: String) -> Unit) {

    val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

    val toastState = rememberToastState()

    val deleteConfirmDialogState = remember { mutableStateOf(false) }

    var desktopSelectedFolderPath by remember { mutableStateOf("") }

    var desktopSelectedFile by remember { mutableStateOf("") }

    var androidSelectedFile by remember { mutableStateOf("") }

    var createAndroidFolderName by remember { mutableStateOf("") }

    var createAndroidFileContent by remember { mutableStateOf("") }

    var createAndroidFileName by remember { mutableStateOf("") }

    BasePage("文件管理器") {
        Row {
            CommonButton(
                onClick = { destinationCall(FileManager.LAST_DIR) }, text = "${Constants.LEFT_ARROW}返回上一级"
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
                modifier = Modifier.padding(vertical = 5.dp).fillMaxWidth(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colors.surface)
                    .padding(5.dp)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(1f)) {
            Column(
                modifier = Modifier.fillMaxHeight(1f).padding(end = 10.dp).weight(0.6f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colors.surface)
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
                                        else
                                            println("点击文件：${it.path}")
                                    }
                                }.fillParentMaxWidth().background(
                                    // android端分隔符固定为/
                                    if (androidSelectedFile.split("/")
                                            .last() == it.path
                                    ) MaterialTheme.colors.onSurface else Color.Transparent
                                )
                            )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(0.4f)
            ) {
                item {
                    Text(
                        "1. 文件操作均会覆盖旧的同名文件，请小心操作，最好提前备份" + "\n" +
                                "2. 文件操作的耗时无法监听，可以一段时间后再次进入刷新",
                        style = infoText,
                        modifier = Modifier.padding(bottom = 10.dp),
                        color = MaterialTheme.colors.onSecondary
                    )
                }
                item {
                    Column(
                        modifier = Modifier.fillParentMaxWidth(1f)
                            .padding(bottom = 10.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colors.surface).padding(10.dp)
                    ) {
                        CenterText("Android内操作", modifier = Modifier.padding(bottom = 10.dp), style = groupTitleText)
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
                                        mainStateHolder.setFileOperationState(FileOperationType.COPY)
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
                                        mainStateHolder.setFileOperationState(FileOperationType.MOVE)
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
                                color = MaterialTheme.colors.error
                            )
                        }

                        SimpleDivider(
                            modifier = Modifier.fillParentMaxWidth(1f).padding(vertical = 10.dp, horizontal = 10.dp)
                                .height(1.dp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            WrappedEditText(
                                value = createAndroidFolderName,
                                tipText = "输入文件夹名称",
                                onValueChange = { createAndroidFolderName = it },
                                modifier = Modifier.padding(horizontal = 10.dp).weight(1f)
                            )

                            CommonButton(
                                text = "创建文件夹",
                                onClick = {
                                    if (createAndroidFolderName.isNotEmpty()) {
                                        mainStateHolder.createDirectory("${directoryState.currentdirectory}/$createAndroidFolderName")
                                        createAndroidFolderName = ""
                                        directoryState.currentdirectory?.let { mainStateHolder.updateFileList(it) }
                                    } else toastState.show("请输入文件夹名称")
                                }
                            )
                        }

                        SimpleDivider(
                            modifier = Modifier.fillParentMaxWidth(1f).padding(vertical = 10.dp, horizontal = 10.dp)
                                .height(1.dp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                WrappedEditText(
                                    value = createAndroidFileName,
                                    tipText = "输入文件名",
                                    onValueChange = { createAndroidFileName = it },
                                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                                )

                                WrappedEditText(
                                    value = createAndroidFileContent,
                                    tipText = "输入文件内容",
                                    onValueChange = { createAndroidFileContent = it },
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                )
                            }

                            CommonButton(
                                text = "创建文件",
                                onClick = {
                                    if (createAndroidFileName.isNotEmpty()) {
                                        mainStateHolder.createFile(
                                            createAndroidFileContent,
                                            "${directoryState.currentdirectory}/$createAndroidFileName"
                                        )
                                        createAndroidFileContent = ""
                                        createAndroidFileName = ""
                                        directoryState.currentdirectory?.let { mainStateHolder.updateFileList(it) }
                                    } else
                                        toastState.show("请输入文件名")
                                }
                            )
                        }
                    }
                }
                item {
                    Column(
                        modifier = Modifier.fillParentMaxWidth(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colors.surface).padding(10.dp)
                    ) {
                        CenterText(
                            "推送Desktop单文件",
                            modifier = Modifier.padding(bottom = 10.dp),
                            style = groupTitleText
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(
                                modifier = Modifier.padding(start = 10.dp, end = 10.dp).weight(1f)
                            ) {
                                CenterText(
                                    text = "选取文件: $desktopSelectedFile",
                                    modifier = Modifier.clickable {
                                        val fileChooser = FileDialog(Frame(), "Select a file", FileDialog.LOAD)
                                        fileChooser.isVisible = true
                                        desktopSelectedFile = fileChooser.directory + fileChooser.file
                                    }.clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colors.secondary)
                                        .border(2.dp, MaterialTheme.colors.onSecondary, RoundedCornerShape(10.dp))
                                        .padding(10.dp)
                                )
                                CenterText(
                                    text = "待接收的Android路径: ${directoryState.currentdirectory}",
                                    modifier = Modifier.padding(10.dp)
                                )
                            }

                            CommonButton(
                                text = "PUSH",
                                modifier = Modifier.width(100.dp),
                                onClick = {
                                    if (desktopSelectedFile.isNotEmpty()) {
                                        toastState.show("开始推送文件，请勿多次点击")
                                        mainStateHolder.pushFileToAndroid(
                                            desktopSelectedFile,
                                            "${directoryState.currentdirectory}/${
                                                desktopSelectedFile.split(PlatformAdapter.sp).last()
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
                        }

                        SimpleDivider(
                            modifier = Modifier.fillParentMaxWidth(1f).padding(vertical = 10.dp, horizontal = 10.dp)
                                .height(1.dp)
                        )

                        CenterText(
                            "推送Desktop的文件夹",
                            modifier = Modifier.padding(bottom = 10.dp),
                            style = groupTitleText
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(
                                modifier = Modifier.padding(start = 10.dp, end = 10.dp).weight(1f)
                            ) {
                                CenterText(
                                    text = "选取文件夹: $desktopSelectedFolderPath",
                                    modifier = Modifier.clickable {
                                        val fileChooser = JFileChooser()
                                        fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                                        // 显示对话框并等待用户选择
                                        val result = fileChooser.showOpenDialog(null);
                                        // 如果用户选择了文件夹
                                        if (result == JFileChooser.APPROVE_OPTION) {
                                            // 获取用户选择的文件夹
                                            desktopSelectedFolderPath = fileChooser.selectedFile.absolutePath
                                        } else {
                                            // 用户取消了选择
                                            toastState.show("No folder selected.");
                                        }
                                    }.clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colors.secondary)
                                        .border(2.dp, MaterialTheme.colors.onSecondary, RoundedCornerShape(10.dp))
                                        .padding(10.dp)
                                )
                                CenterText(
                                    text = "待接收的Android路径: ${directoryState.currentdirectory}",
                                    modifier = Modifier.padding(10.dp)
                                )
                            }

                            CommonButton(
                                text = "PUSH",
                                modifier = Modifier.width(100.dp),
                                onClick = {
                                    if (desktopSelectedFolderPath.isNotEmpty()) {
                                        toastState.show("开始推送文件夹，请勿多次点击")
                                        mainStateHolder.pushFolderToAndroid(
                                            desktopSelectedFolderPath,
                                            "${directoryState.currentdirectory}/${
                                                desktopSelectedFolderPath.split(PlatformAdapter.sp).last()
                                            }"
                                        )
                                    } else {
                                        toastState.show("请选择电脑端要推送到Android的文件夹")
                                    }
                                }
                            )
                        }
                        SimpleDivider(
                            modifier = Modifier.fillParentMaxWidth(1f).padding(vertical = 10.dp, horizontal = 10.dp)
                                .height(1.dp)
                        )
                        // pull界面
                        CenterText(
                            "从Android拉取文件",
                            modifier = Modifier.padding(bottom = 10.dp),
                            style = groupTitleText
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp).weight(1f)) {
                                CenterText(
                                    text = "待拉取的文件: $androidSelectedFile",
                                    modifier = Modifier.padding(10.dp)
                                )

                                CenterText(
                                    text = "默认pull到: ${PlatformAdapter.desktopTempFolder}",
                                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colors.secondary)
                                        .border(2.dp, MaterialTheme.colors.onSecondary, RoundedCornerShape(10.dp))
                                        .padding(10.dp)
                                )
                            }
                            CommonButton(
                                text = "PULL",
                                modifier = Modifier.padding(top = 10.dp).width(100.dp),
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

@Composable
fun FileViewItem(path: String, isDirectory: Boolean, modifier: Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(start = 20.dp)
    ) {
        Image(
            painter = if (isDirectory) painterResource(Res.drawable.ic_folder)
            else painterResource(Res.drawable.ic_file),
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary),
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
