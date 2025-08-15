package com.stephen.debugmanager.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragData
import androidx.compose.ui.draganddrop.dragData
import java.io.File
import java.net.URI

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
inline fun FileDragArea(
    crossinline onSelectFile: (String) -> Unit,
    crossinline onSelectFolder: (String) -> Unit,
    content: @Composable () -> Unit
) {

    val callback = remember {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                val dragData = event.dragData()
                if (dragData is DragData.FilesList) {
                    dragData.readFiles().firstOrNull()?.let { filePath ->
                        val file = File(URI.create(filePath))
                        if (file.isFile) {
                            println("file：${file.absolutePath}")
                            onSelectFile(file.absolutePath)
                        } else if (file.isDirectory) {
                            println("folder：${file.absolutePath}")
                            onSelectFolder(file.absolutePath)
                        }
                    }
                }
                return true
            }
        }
    }

    Box(
        modifier = Modifier.dragAndDropTarget(
            shouldStartDragAndDrop = { event -> true },
            target = callback
        )
    ) {
        content()
    }
}