package com.stephen.debugmanager.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.stephen.debugmanager.ui.component.BasePage
import com.stephen.debugmanager.ui.component.CenterText
import com.stephen.debugmanager.ui.component.DeviceNoneConnectShade
import com.stephen.debugmanager.ui.theme.groupTitleText

@Composable
fun PerformancePage(isDeviceConnected: Boolean) {
    BasePage("性能测试") {
        Box {
            Row {
                Column(
                    modifier = Modifier.fillMaxHeight(1f).weight(0.3f).padding(end = 10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(10.dp)
                ) {
                    CenterText(
                        "系统概览",
                        modifier = Modifier.padding(bottom = 10.dp),
                        style = groupTitleText
                    )
                }
                Column(
                    modifier = Modifier.fillMaxHeight(1f).weight(0.7f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(10.dp)
                ) {
                    CenterText(
                        "APP性能",
                        modifier = Modifier.padding(bottom = 10.dp),
                        style = groupTitleText
                    )
                }
            }
            if (isDeviceConnected.not()) {
                DeviceNoneConnectShade()
            }
        }
    }
}
