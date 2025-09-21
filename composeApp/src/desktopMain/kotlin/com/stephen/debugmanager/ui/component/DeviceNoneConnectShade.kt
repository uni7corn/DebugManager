package com.stephen.debugmanager.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.stephen.composeapp.generated.resources.Res
import com.stephen.composeapp.generated.resources.device_not_connect_tip
import org.jetbrains.compose.resources.stringResource

@Composable
fun DeviceNoneConnectShade() {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    CenterText(
        stringResource(Res.string.device_not_connect_tip), modifier = Modifier.fillMaxSize(1f).background(
            MaterialTheme.colorScheme.background.copy(alpha = 0.8f)
        ).clickable(indication = null, interactionSource = interactionSource) {
            // 屏蔽掉鼠标点击事件
        }
    )
}