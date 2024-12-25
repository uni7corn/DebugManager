package ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ui.component.BasePage
import ui.component.CenterText
import ui.theme.groupBackGroundColor
import ui.theme.groupTitleText

@Composable
fun PerformancePage() {
    BasePage("性能测试") {
        Row {
            Column(
                modifier = Modifier.fillMaxHeight(1f).weight(0.3f).padding(end = 10.dp)
                    .clip(RoundedCornerShape(10.dp)).background(groupBackGroundColor)
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
                    .clip(RoundedCornerShape(10.dp)).background(groupBackGroundColor)
                    .padding(10.dp)
            ) {
                CenterText(
                    "APP性能",
                    modifier = Modifier.padding(bottom = 10.dp),
                    style = groupTitleText
                )
            }
        }
    }
}
