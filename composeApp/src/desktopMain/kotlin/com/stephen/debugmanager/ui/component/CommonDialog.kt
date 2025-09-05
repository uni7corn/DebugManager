package com.stephen.debugmanager.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.stephen.composeapp.generated.resources.Res
import com.stephen.composeapp.generated.resources.dialog_cancel
import com.stephen.composeapp.generated.resources.dialog_confirm
import org.jetbrains.compose.resources.stringResource

@Composable
fun CommonDialog(title: String, onConfirm: () -> Unit, onCancel: () -> Unit, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier.width(240.dp).clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CenterText(title, modifier = Modifier.fillMaxWidth(1f).padding(10.dp))
            }
            Row(
                modifier = Modifier.height(40.dp).fillMaxWidth(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    SimpleDivider(Modifier.height(1.dp).fillMaxWidth(1f))
                    Row {
                        CenterText(
                            stringResource(Res.string.dialog_cancel),
                            modifier = Modifier.clickable { onCancel() }.weight(1f).fillMaxHeight(1f)
                        )
                        SimpleDivider(Modifier.width(1.dp).fillMaxHeight(1f))
                        CenterText(
                            stringResource(Res.string.dialog_confirm),
                            modifier = Modifier.clickable { onConfirm() }.weight(1f).fillMaxHeight(1f)
                        )
                    }
                }
            }
        }
    }
}