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
import androidx.compose.ui.unit.dp
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import com.stephen.composeapp.generated.resources.Res
import com.stephen.composeapp.generated.resources.performance_page_app_performance
import com.stephen.composeapp.generated.resources.performance_page_cpu
import com.stephen.composeapp.generated.resources.performance_page_cpu_host
import com.stephen.composeapp.generated.resources.performance_page_cpu_idle
import com.stephen.composeapp.generated.resources.performance_page_cpu_io
import com.stephen.composeapp.generated.resources.performance_page_cpu_irq
import com.stephen.composeapp.generated.resources.performance_page_cpu_nice
import com.stephen.composeapp.generated.resources.performance_page_cpu_overview
import com.stephen.composeapp.generated.resources.performance_page_cpu_sirq
import com.stephen.composeapp.generated.resources.performance_page_cpu_sys
import com.stephen.composeapp.generated.resources.performance_page_cpu_total
import com.stephen.composeapp.generated.resources.performance_page_cpu_user
import com.stephen.composeapp.generated.resources.performance_page_memory_free
import com.stephen.composeapp.generated.resources.performance_page_memory_overview
import com.stephen.composeapp.generated.resources.performance_page_memory_total
import com.stephen.composeapp.generated.resources.performance_page_memory_used
import com.stephen.composeapp.generated.resources.performance_page_physical_memory
import com.stephen.composeapp.generated.resources.performance_page_pid
import com.stephen.composeapp.generated.resources.performance_page_process_name
import com.stephen.composeapp.generated.resources.performance_page_user_id
import com.stephen.debugmanager.MainStateHolder
import com.stephen.debugmanager.data.bean.PackageInfo
import com.stephen.debugmanager.data.uistate.ProcessPerfState
import com.stephen.debugmanager.ui.component.BasePage
import com.stephen.debugmanager.ui.component.CenterText
import com.stephen.debugmanager.ui.component.DeviceNoneConnectShade
import com.stephen.debugmanager.ui.component.NameValueText
import com.stephen.debugmanager.ui.theme.defaultText
import com.stephen.debugmanager.ui.theme.groupTitleText
import com.stephen.debugmanager.ui.theme.itemKeyText
import com.stephen.debugmanager.ui.theme.itemValueText
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.core.context.GlobalContext
import java.io.File

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PerformancePage(isDeviceConnected: Boolean, appListState: List<PackageInfo>) {

    val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

    val performanceState = mainStateHolder.performanceStateStateFlow.collectAsState()

    val prcessPerfListState = mainStateHolder.processPerfListStateStateFlow.collectAsState()

    var selectedApp by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        launch {
            mainStateHolder.getTotalPerformanceResult()
        }
        launch {
            mainStateHolder.startLoopGetProcessPerf()
        }
    }

    BasePage({
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
                            stringResource(Res.string.performance_page_cpu_overview),
                            modifier = Modifier.padding(bottom = 20.dp),
                            style = groupTitleText
                        )
                        Column {
                            NameValueText(
                                stringResource(Res.string.performance_page_cpu_total),
                                performanceState.value.cpuTotal
                            )
                            NameValueText(
                                stringResource(Res.string.performance_page_cpu_user),
                                performanceState.value.cpuUser
                            )
                            NameValueText(
                                stringResource(Res.string.performance_page_cpu_sys),
                                performanceState.value.cpuSys
                            )
                            NameValueText(
                                stringResource(Res.string.performance_page_cpu_nice),
                                performanceState.value.cpuNice
                            )
                            NameValueText(
                                stringResource(Res.string.performance_page_cpu_idle),
                                performanceState.value.cpuIdle
                            )
                            NameValueText(
                                stringResource(Res.string.performance_page_cpu_io),
                                performanceState.value.cpuIOWait
                            )
                            NameValueText(
                                stringResource(Res.string.performance_page_cpu_irq),
                                performanceState.value.cpuIRQ
                            )
                            NameValueText(
                                stringResource(Res.string.performance_page_cpu_sirq),
                                performanceState.value.cpuSoftIRQ
                            )
                            NameValueText(
                                stringResource(Res.string.performance_page_cpu_host),
                                performanceState.value.cpuHost
                            )
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
                            stringResource(Res.string.performance_page_memory_overview),
                            modifier = Modifier.padding(bottom = 20.dp),
                            style = groupTitleText
                        )
                        Column {
                            NameValueText(
                                stringResource(Res.string.performance_page_memory_total),
                                performanceState.value.memTotal
                            )
                            NameValueText(
                                stringResource(Res.string.performance_page_memory_free),
                                performanceState.value.memFree
                            )
                            NameValueText(
                                stringResource(Res.string.performance_page_memory_used),
                                performanceState.value.memUsed
                            )
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
                        stringResource(Res.string.performance_page_app_performance),
                        modifier = Modifier.padding(bottom = 10.dp),
                        style = groupTitleText
                    )
                    LazyColumn {
                        items(appListState, key = { it }) {
                            Box(
                                modifier = Modifier.fillMaxWidth(1f).animateItem()
                            ) {
                                PerformanceAppItem(
                                    it.packageName,
                                    it.label,
                                    it.versionName,
                                    mainStateHolder.getIconFilePath(it.packageName),
                                    isNeedToExpand = (selectedApp == it.packageName),
                                    perfState = prcessPerfListState.value,
                                    onClick = { item ->
                                        selectedApp = item
                                        mainStateHolder.setProcessPackage(item)
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
    })
}


@Composable
fun PerformanceAppItem(
    packageName: String,
    label: String,
    version: String,
    iconFilePath: String,
    perfState: MutableList<ProcessPerfState>,
    isNeedToExpand: Boolean = false,
    onClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier.animateContentSize().fillMaxWidth(1f).padding(vertical = 5.dp)
            .border(2.dp, MaterialTheme.colorScheme.onSecondary, RoundedCornerShape(10.dp))
            .clickable {
                onClick(packageName)
            }.padding(5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val coilAsyncPainter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(File(iconFilePath))
                    .build()
            )
            Image(
                painter = coilAsyncPainter,
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
            Column(modifier = Modifier.fillMaxWidth(1f).padding(top = 10.dp)) {
                Row(modifier = Modifier.padding(bottom = 10.dp)) {
                    CenterText(text = stringResource(Res.string.performance_page_user_id), style = itemKeyText, modifier = Modifier.weight(1f))
                    CenterText(text = stringResource(Res.string.performance_page_pid), style = itemKeyText, modifier = Modifier.weight(1f))
                    CenterText(text = stringResource(Res.string.performance_page_physical_memory), style = itemKeyText, modifier = Modifier.weight(1f))
                    CenterText(text = stringResource(Res.string.performance_page_cpu), style = itemKeyText, modifier = Modifier.weight(1f))
                    CenterText(text = stringResource(Res.string.performance_page_process_name), style = itemKeyText, modifier = Modifier.weight(3f))
                }
                perfState.forEach {
                    Row(modifier = Modifier.padding(bottom = 10.dp)) {
                        CenterText(text = it.userId, style = itemValueText, modifier = Modifier.weight(1f))
                        CenterText(text = it.pid, style = itemValueText, modifier = Modifier.weight(1f))
                        CenterText(text = it.rss, style = itemValueText, modifier = Modifier.weight(1f))
                        CenterText(text = it.cpu, style = itemValueText, modifier = Modifier.weight(1f))
                        CenterText(text = it.processName, style = itemValueText, modifier = Modifier.weight(3f))
                    }
                }
            }
        }
    }
}