package com.stephen.debugmanager.ui.pages

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import com.stephen.debugmanager.MainStateHolder
import com.stephen.debugmanager.data.uistate.AppListState
import com.stephen.debugmanager.data.uistate.ProcessPerfState
import com.stephen.debugmanager.ui.component.BasePage
import com.stephen.debugmanager.ui.component.CenterText
import com.stephen.debugmanager.ui.component.DeviceNoneConnectShade
import com.stephen.debugmanager.ui.component.NameValueText
import com.stephen.debugmanager.ui.theme.defaultText
import com.stephen.debugmanager.ui.theme.groupTitleText
import com.stephen.debugmanager.ui.theme.itemKeyText
import com.stephen.debugmanager.ui.theme.itemValueText
import org.koin.core.context.GlobalContext

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PerformancePage(isDeviceConnected: Boolean, appListState: AppListState) {

    val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

    val performanceState = mainStateHolder.performanceStateStateFlow.collectAsState()

    var prcessPerfListState = remember { mutableListOf<ProcessPerfState>() }

    var choosedApp by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        mainStateHolder.getTotalPerformanceResult()
    }

    LaunchedEffect(choosedApp) {
        println("change app: $choosedApp")
        prcessPerfListState = mainStateHolder.getProcessPerformanceResult(choosedApp)
    }

    BasePage("性能测试") {
        Box {
            Row {
                Column(
                    modifier = Modifier.fillMaxHeight(1f)
                        .width(IntrinsicSize.Max)
                        .padding(end = 10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .padding(bottom = 10.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(10.dp)
                    ) {
                        CenterText(
                            "CPU概览",
                            modifier = Modifier.padding(bottom = 20.dp),
                            style = groupTitleText
                        )
                        Column {
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
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(10.dp)
                    ) {
                        CenterText(
                            "内存概览",
                            modifier = Modifier.padding(bottom = 20.dp),
                            style = groupTitleText
                        )
                        Column {
                            NameValueText("Total总量", performanceState.value.memTotal)
                            NameValueText("Free空闲", performanceState.value.memFree)
                            NameValueText("Used使用", performanceState.value.memUsed)
                        }
                    }
                }

                Column(
                    modifier = Modifier.weight(0.6f)
                        .fillMaxHeight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(10.dp)
                ) {
                    CenterText(
                        "APP性能",
                        modifier = Modifier.padding(bottom = 10.dp),
                        style = groupTitleText
                    )
                    LazyColumn {
                        items(appListState.appList, key = { it.packageName }) {
                            Box(
                                modifier = Modifier.fillMaxWidth(1f).animateItem()
                            ) {
                                PerformanceAppItem(
                                    it.packageName,
                                    it.appLabel,
                                    it.version,
                                    it.icon,
                                    isNeedToExpand = (choosedApp == it.packageName),
                                    perfState = prcessPerfListState,
                                    onClick = {
                                        choosedApp = it
                                    }
                                )
                            }
                        }
                    }
                }
            }
            if (isDeviceConnected.not()) {
                DeviceNoneConnectShade()
            }
        }
    }
}


@Composable
fun PerformanceAppItem(
    packageName: String,
    label: String,
    version: String,
    iconBitmap: ImageBitmap,
    perfState: List<ProcessPerfState>,
    isNeedToExpand: Boolean = false,
    onClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier.animateContentSize().fillMaxWidth(1f).padding(vertical = 5.dp)
            .border(2.dp, MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(10.dp))
            .clickable {
                onClick(packageName)
            }.padding(5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            Image(
                painter = BitmapPainter(image = iconBitmap),
                modifier = Modifier.padding(start = 5.dp).size(50.dp),
                contentDescription = "app icon"
            )

            Column(modifier = Modifier.padding(start = 10.dp).weight(0.4f)) {
                CenterText(text = label, style = itemKeyText)
                CenterText(text = version, style = defaultText)
                SelectionContainer {
                    CenterText(text = packageName, style = defaultText)
                }
            }
        }

        if (isNeedToExpand) {
            Column(modifier = Modifier.fillMaxWidth(1f)) {
                Row(modifier = Modifier.padding(bottom = 10.dp)) {
                    CenterText(text = "用户ID", style = itemKeyText, modifier = Modifier.weight(2f))
                    CenterText(text = "PID", style = itemKeyText, modifier = Modifier.weight(1f))
                    CenterText(text = "虚拟内存", style = itemKeyText, modifier = Modifier.weight(2f))
                    CenterText(text = "物理内存", style = itemKeyText, modifier = Modifier.weight(2f))
                    CenterText(text = "CPU", style = itemKeyText, modifier = Modifier.weight(1f))
                    CenterText(text = "进程名", style = itemKeyText, modifier = Modifier.weight(6f))
                }
                perfState.forEach {
                    Row(modifier = Modifier.padding(bottom = 10.dp)) {
                        CenterText(text = it.userId, style = itemValueText, modifier = Modifier.weight(2f))
                        CenterText(text = it.pid, style = itemValueText, modifier = Modifier.weight(1f))
                        CenterText(text = it.vsz, style = itemValueText, modifier = Modifier.weight(2f))
                        CenterText(text = it.rss, style = itemValueText, modifier = Modifier.weight(2f))
                        CenterText(text = it.cpu, style = itemValueText, modifier = Modifier.weight(1f))
                        CenterText(text = it.processName, style = itemValueText, modifier = Modifier.weight(6f))
                    }
                }
            }
        }
    }
}