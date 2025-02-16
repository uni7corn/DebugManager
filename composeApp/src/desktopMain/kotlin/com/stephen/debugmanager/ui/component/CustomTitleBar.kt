package com.stephen.debugmanager.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import com.stephen.composeapp.generated.resources.Res
import com.stephen.composeapp.generated.resources.app_logo
import com.stephen.composeapp.generated.resources.ic_close
import com.stephen.composeapp.generated.resources.ic_floating
import com.stephen.composeapp.generated.resources.ic_maximize
import com.stephen.composeapp.generated.resources.ic_minimize
import com.stephen.debugmanager.ui.theme.groupBackGroundColor
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
            .background(groupBackGroundColor),
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
                    painter = painterResource(Res.drawable.app_logo),
                    modifier = Modifier.padding(5.dp).clip(RoundedCornerShape(10))
                )
                CenterText(text = title, modifier = Modifier.padding(start = 4.dp))
            }

            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                Image(
                    contentDescription = "minimize",
                    painter = painterResource(Res.drawable.ic_minimize),
                    modifier = Modifier.clickable { onMinimize() }.size(40.dp).padding(5.dp)
                )
                Image(
                    contentDescription = "maximize",
                    painter = if (isMaximized) painterResource(Res.drawable.ic_floating)
                    else painterResource(Res.drawable.ic_maximize),
                    modifier = Modifier.clickable { onMaximize() }.size(40.dp).padding(5.dp)
                )
                Image(
                    contentDescription = "close",
                    painter = painterResource(Res.drawable.ic_close),
                    modifier = Modifier.clickable { onClose() }.size(40.dp).padding(2.dp)
                )
            }
        }
    }
}