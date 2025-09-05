package com.stephen.debugmanager.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stephen.debugmanager.MainStateHolder
import com.stephen.debugmanager.data.Constants
import com.stephen.debugmanager.data.Constants.mainItemMap
import com.stephen.debugmanager.data.bean.MainTabItem
import com.stephen.debugmanager.data.uistate.DirectoryState
import com.stephen.debugmanager.ui.component.CenterText
import com.stephen.debugmanager.ui.component.DropdownSelector
import com.stephen.debugmanager.ui.component.SimpleDivider
import com.stephen.debugmanager.ui.pages.*
import com.stephen.debugmanager.ui.theme.defaultText
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.context.GlobalContext

@Composable
fun ContentView(isMenuExpanded: Boolean) {

    val choosedTab = remember { mutableStateOf(mainItemMap.keys.toList()[0]) }

    val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

    val deviceMapState by mainStateHolder.deviceMapStateStateFlow.collectAsState()

    val deviceState by mainStateHolder.deviceStateStateFlow.collectAsState()

    val directoryState by mainStateHolder.directoryStateStateFlow.collectAsState()

    val appListState by mainStateHolder.appListStateStateFlow

    LaunchedEffect(Unit) {
        mainStateHolder.getDeviceMap()
        mainStateHolder.getCurrentDeviceInfo()
    }

    Row(modifier = Modifier.fillMaxSize(1f)) {
        val navController = rememberNavController()
        AnimatedContent(targetState = isMenuExpanded) { expanded ->
            if (expanded) {
                Row {
                    SideTabBar(
                        deviceMapState.deviceMap,
                        deviceMapState.choosedSerial,
                        onDeviceSelect = {
                            mainStateHolder.setChooseDevice(it)
                            mainStateHolder.getCurrentDeviceInfo()
                        },
                        mainItemMap,
                        onItemClick = {
                            // 检查当前是否已经在目标页面，如果不是则进行导航
                            if (navController.currentDestination?.route != it) {
                                navController.navigate(it)
                            }
                            choosedTab.value = it
                        },
                        chooseTabItem = choosedTab.value,
                        modifier = Modifier.fillMaxHeight(1f).animateContentSize()
                    )
                    SimpleDivider(modifier = Modifier.width(2.dp).fillMaxHeight(1f))
                }
            }
        }
        // 右侧内容区
        Box(modifier = Modifier.weight(1f).animateContentSize()) {
            NavHost(navController, startDestination = Constants.DEVICE_INFO) {
                composable(Constants.DEVICE_INFO) {
                    DeviceInfoPage(
                        deviceState,
                        onRefresh = {
                            mainStateHolder.getCurrentDeviceInfo()
                            mainStateHolder.getpackageListInfo()
                        })
                }
                composable(Constants.APP_MANAGE) {
                    ApkManagePage(appListState, deviceState.isConnected) {
                        mainStateHolder.getpackageListInfo(it)
                    }
                }
                composable(Constants.FILE_MANAGE) {
                    FileManagePage(
                        DirectoryState(
                            directoryState.deviceCode,
                            directoryState.currentdirectory,
                            directoryState.subdirectories,
                        ),
                        deviceState.isConnected,
                        destinationCall = { destination ->
                            mainStateHolder.getFileList(destination)
                        }
                    )
                }
                composable(Constants.COMMAND) {
                    CommandPage(deviceState.isConnected)
                }
                composable(Constants.PERFORMANCE) {
                    PerformancePage(deviceState.isConnected, appListState)
                }
                composable(Constants.TOOLS) {
                    ToolsPage()
                }
                composable(Constants.ABOUT) {
                    AboutPage()
                }
                composable(Constants.AI_MODEL) {
                    AiModelPage()
                }
            }
        }
    }
}

@Composable
fun SideTabBar(
    deviceMap: Map<String, String>,
    serialNumber: String,
    onDeviceSelect: (String) -> Unit,
    mainItemMap: Map<String, MainTabItem>,
    onItemClick: (name: String) -> Unit,
    chooseTabItem: String,
    modifier: Modifier = Modifier
) {
    val sideBarWidth = 180.dp

    LazyColumn(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {
        item {
            Column {
                DropdownSelector(
                    deviceMap,
                    // 当拔掉设备，map长度减少时，防止deviceSelectedPosition越界，设为map长度为其最大值
                    serialNumber,
                    modifier = Modifier.width(sideBarWidth),
                ) {
                    // 返回选中的设备位置，0123
                    onDeviceSelect(it)
                }
                SimpleDivider(Modifier.width(sideBarWidth).height(1.dp))
                Spacer(Modifier.height(40.dp))
            }
        }
        item {
            Column(Modifier.width(sideBarWidth)) {
                mainItemMap.keys.forEach { itemCode ->
                    mainItemMap[itemCode]?.let { item ->
                        SideTabItem(
                            icon = item.icon,
                            title = stringResource(item.name),
                            isSelected = chooseTabItem == itemCode,
                            modifier = Modifier.fillMaxWidth(1f).clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) {
                                onItemClick(itemCode)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SideTabItem(icon: DrawableResource, title: String, modifier: Modifier, isSelected: Boolean) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.padding(3.dp).clip(RoundedCornerShape(10))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .padding(vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.weight(1f))
            Image(
                painter = painterResource(icon),
                modifier = Modifier.padding(end = 10.dp).weight(2f).size(20.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
                contentDescription = "tab_icon"
            )
            CenterText(
                title,
                style = defaultText,
                modifier = Modifier.weight(6f),
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
            )
            Spacer(Modifier.weight(1f))
        }
    }
}