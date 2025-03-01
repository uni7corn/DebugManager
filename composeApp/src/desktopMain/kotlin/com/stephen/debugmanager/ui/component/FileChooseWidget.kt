package com.stephen.debugmanager.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragData
import androidx.compose.ui.draganddrop.dragData
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.stephen.debugmanager.ui.theme.defaultText
import com.stephen.debugmanager.utils.LogUtils
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.net.URI
import javax.swing.JFileChooser

/**
 * @param tintText 提示文本
 * @param path 路径
 * @param onPathSelect 路径选择回调
 * @param isChooseFile 是否选择文件，默认为 false
 * @param fileType 文件类型
 * @param onErrorOccur 错误消息回调
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun FileChooseWidget(
    tintText: String,
    path: String,
    modifier: Modifier = Modifier,
    isChooseFile: Boolean = false,
    fileType: String = "",
    onErrorOccur: (String) -> Unit = {},
    onPathSelect: (String) -> Unit,
) {

    val callback = remember {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                val dragData = event.dragData()
                if (dragData is DragData.FilesList) {
                    dragData.readFiles().firstOrNull()?.let { filePath ->
                        val file = File(URI.create(filePath))
                        LogUtils.printLog("选取文件：${file.absolutePath}")
                        if (fileType.isNotEmpty() && fileType.split('.').last() != file.extension) {
                            onErrorOccur("请选择正确的文件类型")
                            return false
                        }
                        onPathSelect(file.absolutePath)
                    }
                }
                return true
            }
        }
    }

    CenterText(
        text = path.ifEmpty { tintText },
        style = defaultText,
        modifier = modifier.border(2.dp, MaterialTheme.colorScheme.onSecondary, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.secondary).clickable {
                // 选择文件
                if (isChooseFile) {
                    val fileChooser = FileDialog(
                        Frame(),
                        "Select a file",
                        FileDialog.LOAD
                    ).apply {
                        file = fileType
                    }
                    fileChooser.isVisible = true
                    // 判断是否未选文件
                    if (fileChooser.file != null) {
                        onPathSelect(fileChooser.directory + fileChooser.file)
                    }
                } else {
                    // 选择文件夹
                    val fileChooser = JFileChooser()
                    fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                    // 显示对话框并等待用户选择
                    val result = fileChooser.showOpenDialog(null);
                    // 如果用户选择了文件夹
                    if (result == JFileChooser.APPROVE_OPTION) {
                        // 获取用户选择的文件夹
                        onPathSelect(fileChooser.selectedFile.absolutePath)
                    }
                }
            }.dragAndDropTarget(
                shouldStartDragAndDrop = { event -> true },
                target = callback
            ).padding(10.dp)
    )
}