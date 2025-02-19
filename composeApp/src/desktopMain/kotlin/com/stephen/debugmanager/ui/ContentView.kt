package com.stephen.debugmanager.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stephen.composeapp.generated.resources.*
import com.stephen.debugmanager.MainStateHolder
import com.stephen.debugmanager.data.Constants
import com.stephen.debugmanager.data.bean.MainTabItem
import com.stephen.debugmanager.data.uistate.DirectoryState
import com.stephen.debugmanager.ui.component.CenterText
import com.stephen.debugmanager.ui.component.CommonDialog
import com.stephen.debugmanager.ui.component.SimpleDivider
import com.stephen.debugmanager.ui.component.DropdownSelector
import com.stephen.debugmanager.ui.pages.*
import com.stephen.debugmanager.ui.theme.pageTitleText
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.GlobalContext

@Composable
fun ContentView(onExitApplication: () -> Unit) {

    val mainItemList = listOf(
        Constants.BASE_INFO to Res.drawable.ic_devices,
        Constants.INSTALL to Res.drawable.ic_software,
        Constants.FILE_MANAGE to Res.drawable.ic_file_manage,
        Constants.COMMAND to Res.drawable.ic_terminal,
        Constants.PERFORMANCE to Res.drawable.ic_performance,
        Constants.ABOUT to Res.drawable.ic_about,
        Constants.AI_MODEL to Res.drawable.ic_about,
    ).map { (name, icon) -> MainTabItem(name, icon) }

    val choosedTab = remember { mutableStateOf(mainItemList[0]) }

    val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

    val deviceMapState by mainStateHolder.deviceMapStateStateFlow.collectAsState()

    val deviceState by mainStateHolder.deviceStateStateFlow.collectAsState()

    val directoryState by mainStateHolder.directoryStateStateFlow.collectAsState()

    val appListState by mainStateHolder.appListStateStateFlow.collectAsState()

    LaunchedEffect(Unit) {
        mainStateHolder.getDeviceMap()
        mainStateHolder.getCurrentDeviceInfo()
    }

    if (!deviceState.isConnected) {
        CommonDialog(
            "识别失败，ADB服务正在启动，请检查调试模式是否开启，USB线是否接好。点击确认按钮重试，点击取消退出程序",
            onConfirm = {
                mainStateHolder.getCurrentDeviceInfo()
            },
            onCancel = {
                onExitApplication()
            },
            onDismiss = {}
        )
    }

    Row(modifier = Modifier.fillMaxSize(1f)) {
        val navController = rememberNavController()
        SideTabBar(
            deviceMapState.deviceMap,
            deviceMapState.currentChoosedDevice.toString(),
            onDeviceSelect = {
                mainStateHolder.setChooseDevice(it.toInt())
                mainStateHolder.getCurrentDeviceInfo()
            },
            mainItemList,
            onItemClick = {
                navController.navigate(it.name)
                choosedTab.value = it
            },
            chooseTabItem = choosedTab.value,
        )
        SimpleDivider(modifier = Modifier.width(2.dp).fillMaxHeight(1f))
        // 右侧内容区
        Box(modifier = Modifier.weight(1f)) {
            NavHost(navController, startDestination = Constants.BASE_INFO.toString()) {
                composable(Constants.BASE_INFO.toString()) {
                    DeviceInfoPage(
                        deviceState,
                        onRefresh = {
                            mainStateHolder.getCurrentDeviceInfo()
                        })
                }
                composable(Constants.INSTALL.toString()) {
                    ApkManagePage(appListState) {
                        mainStateHolder.getPackageList(it)
                    }
                }
                composable(Constants.FILE_MANAGE.toString()) {
                    FileManagePage(
                        DirectoryState(
                            directoryState.deviceCode,
                            directoryState.currentdirectory,
                            directoryState.subdirectories,
                        ),
                        destinationCall = { destination ->
                            mainStateHolder.getFileList(destination)
                        }
                    )
                }
                composable(Constants.COMMAND.toString()) {
                    CommandPage()
                }
                composable(Constants.PERFORMANCE.toString()) {
                    PerformancePage()
                }
                composable(Constants.ABOUT.toString()) {
                    AboutPage()
                }
                composable(Constants.AI_MODEL.toString()) {
                    AiModelPage()
                }
            }
        }
    }
}

@Composable
fun SideTabBar(
    deviceMap: Map<String, String>,
    deviceSelectedPosition: String,
    onDeviceSelect: (String) -> Unit,
    mainItemList: List<MainTabItem>,
    onItemClick: (name: MainTabItem) -> Unit,
    chooseTabItem: MainTabItem
) {
    LazyColumn {
        item {
            DropdownSelector(
                deviceMap,
                // 当拔掉设备，map长度减少时，防止deviceSelectedPosition越界，设为map长度为其最大值
                deviceSelectedPosition.toInt().coerceAtMost(deviceMap.size - 1).toString(),
                modifier = Modifier.width(160.dp)
            ) {
                // 返回选中的设备位置，0123
                onDeviceSelect(it)
            }
        }
        item {
            Spacer(Modifier.height(40.dp))
        }
        item {
            Column(Modifier.width(160.dp)) {
                mainItemList.forEach { it ->
                    SideTabItem(
                        icon = it.icon,
                        title = it.name,
                        modifier = Modifier.fillMaxWidth(1f).clip(RoundedCornerShape(10))
                            .background(if (chooseTabItem == it) MaterialTheme.colors.surface else MaterialTheme.colors.background)
                            .clickable {
                                onItemClick(it)
                            },
                    )
                }
            }
        }
    }
}

@Composable
fun SideTabItem(icon: DrawableResource, title: String, modifier: Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.padding(vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(icon),
                modifier = Modifier.padding(end = 10.dp).size(28.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary),
                contentDescription = "tab_icon"
            )
            CenterText(title, style = pageTitleText)
        }
    }
}