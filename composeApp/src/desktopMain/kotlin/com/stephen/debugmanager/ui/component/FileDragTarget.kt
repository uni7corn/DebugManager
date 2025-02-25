package com.stephen.debugmanager.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragData
import androidx.compose.ui.draganddrop.dragData
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import java.io.File
import java.net.URI
import kotlin.text.ifEmpty

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun FileDragTarget(modifier: Modifier) {

    var choosedFilePath by remember { mutableStateOf("") }

    val callback = remember {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                val dragData = event.dragData()
                if (dragData is DragData.FilesList) {
                    dragData.readFiles().firstOrNull()?.let { filePath ->
                        val file = File(URI.create(filePath))
                        println(file.absolutePath)
                        choosedFilePath = file.absolutePath
                    }
                }
                return true
            }
        }
    }

    Box(
        modifier = modifier.clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(10.dp).dragAndDropTarget(
                shouldStartDragAndDrop = { event -> true },
                target = callback
            ),
        contentAlignment = Alignment.Center
    ) {
        CenterText(text = choosedFilePath.ifEmpty { "拖拽文件到此处" })
    }
}