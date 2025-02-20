package com.stephen.debugmanager.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import com.stephen.composeapp.generated.resources.Res
import com.stephen.composeapp.generated.resources.ic_close
import com.stephen.composeapp.generated.resources.ic_compose
import com.stephen.composeapp.generated.resources.ic_floating
import com.stephen.composeapp.generated.resources.ic_maximize
import com.stephen.composeapp.generated.resources.ic_minimize
import org.jetbrains.compose.resources.painterResource

@Composable
fun CustomTitleBar(
    title: String,
    windowState: WindowState,
    onClose: () -> Unit,
    onMinimize: () -> Unit = {
        windowState.isMinimized = true
    },
    onMaximize: () -> Unit = {
        windowState.placement = if (windowState.placement == WindowPlacement.Maximized)
            WindowPlacement.Floating else WindowPlacement.Maximized
    },
    modifier: Modifier = Modifier
) {
    val isMaximized = windowState.placement == WindowPlacement.Maximized
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp)
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    contentDescription = "logo",
                    painter = painterResource(Res.drawable.ic_compose),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                    modifier = Modifier.padding(5.dp).clip(RoundedCornerShape(10))
                )
                CenterText(text = title, modifier = Modifier.padding(start = 4.dp))
            }

            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                Image(
                    contentDescription = "minimize",
                    painter = painterResource(Res.drawable.ic_minimize),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                    modifier = Modifier.clickable { onMinimize() }.size(32.dp).padding(5.dp)
                )
                Image(
                    contentDescription = "maximize",
                    painter = if (isMaximized) painterResource(Res.drawable.ic_floating)
                    else painterResource(Res.drawable.ic_maximize),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                    modifier = Modifier.clickable { onMaximize() }.size(32.dp).padding(5.dp)
                )
                Image(
                    contentDescription = "close",
                    painter = painterResource(Res.drawable.ic_close),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.error),
                    modifier = Modifier.clickable { onClose() }.size(32.dp).padding(5.dp)
                )
            }
        }
    }
}