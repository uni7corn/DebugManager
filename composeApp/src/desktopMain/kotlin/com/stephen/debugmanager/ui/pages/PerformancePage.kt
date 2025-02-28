package com.stephen.debugmanager.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.stephen.debugmanager.MainStateHolder
import com.stephen.debugmanager.ui.component.BasePage
import com.stephen.debugmanager.ui.component.CenterText
import com.stephen.debugmanager.ui.component.DeviceNoneConnectShade
import com.stephen.debugmanager.ui.component.NameValueText
import com.stephen.debugmanager.ui.theme.groupTitleText
import org.koin.core.context.GlobalContext

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PerformancePage(isDeviceConnected: Boolean) {

    val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

    val performanceState = mainStateHolder.performanceStateStateFlow.collectAsState()

    LaunchedEffect(Unit) {
        mainStateHolder.getTotalPerformanceResult()
    }

    BasePage("性能测试") {
        Box {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(
                    modifier = Modifier.width(IntrinsicSize.Max)
                        .fillMaxRowHeight(1f)
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(10.dp)
                ) {
                    CenterText(
                        "CPU概览",
                        modifier = Modifier.padding(bottom = 20.dp),
                        style = groupTitleText
                    )
                    Column(modifier = Modifier.padding(bottom = 20.dp)) {
                        NameValueText("Total总量", performanceState.value.cpuTotal)
                        NameValueText("User用户", performanceState.value.cpuUser)
                        NameValueText("Sys系统", performanceState.value.cpuSys)
                        NameValueText("Nice低优先级", performanceState.value.cpuNice)
                        NameValueText("Idle空闲", performanceState.value.cpuIdle)
                        NameValueText("IO等待", performanceState.value.cpuIOWait)
                        NameValueText("IRQ硬中断", performanceState.value.cpuIRQ)
                        NameValueText("SIRQ软中断", performanceState.value.cpuSoftIRQ)
                        NameValueText("Host虚拟机", performanceState.value.cpuHost)
                    }
                }

                Column(
                    modifier = Modifier.width(IntrinsicSize.Max)
                        .fillMaxRowHeight(1f)
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(10.dp)
                ) {
                    CenterText(
                        "内存概览",
                        modifier = Modifier.padding(bottom = 20.dp),
                        style = groupTitleText
                    )
                    Column(modifier = Modifier.padding(bottom = 10.dp)) {
                        NameValueText("Total总量", performanceState.value.memTotal)
                        NameValueText("Free空闲", performanceState.value.memFree)
                        NameValueText("Used使用", performanceState.value.memUsed)
                    }
                }
                Column(
                    modifier = Modifier.width(IntrinsicSize.Max)
                        .fillMaxRowHeight(1f)
                        .padding(vertical = 5.dp)
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
