package com.stephen.debugmanager.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.stephen.debugmanager.ui.theme.defaultText
import java.awt.FileDialog
import java.awt.Frame
import javax.swing.JFileChooser

/**
 * @param tintText 提示文本
 * @param path 路径
 * @param onPathSelect 路径选择回调
 * @param isChooseFile 是否选择文件，默认为 false
 */
@Composable
fun LocalFileChooser(
    tintText: String,
    path: String,
    modifier: Modifier = Modifier,
    isChooseFile: Boolean = false,
    fileType: String? = "",
    onPathSelect: (String) -> Unit,
) {
    CenterText(
        text = "${tintText}: $path",
        style = defaultText,
        modifier = modifier.border(2.dp, MaterialTheme.colors.onSecondary, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colors.secondary).clickable {
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
                    onPathSelect(fileChooser.directory + fileChooser.file)
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
            }.padding(horizontal = 10.dp, vertical = 5.dp)
    )
}