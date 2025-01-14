package com.stephen.debugmanager.ui

import MainStateHolder
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.stephen.debugmanager.data.Constants
import com.stephen.debugmanager.data.bean.MainTabItem
import com.stephen.debugmanager.model.uistate.DirectoryState
import com.stephen.debugmanager.ui.component.CenterText
import com.stephen.debugmanager.ui.component.CommonDialog
import com.stephen.debugmanager.ui.component.DarkDivider
import com.stephen.debugmanager.ui.component.DropdownSelector
import com.stephen.debugmanager.ui.pages.*
import com.stephen.debugmanager.ui.theme.backGroundColor
import com.stephen.debugmanager.ui.theme.groupBackGroundColor
import com.stephen.debugmanager.ui.theme.pageTitleText
import org.koin.core.context.GlobalContext

@Composable
fun ContentView(onExitApplication: () -> Unit) {

    val mainItemList = listOf(
        MainTabItem(Constants.BAME_INFO, "基本信息"),
        MainTabItem(Constants.INSTALL, "软件管理"),
        MainTabItem(Constants.FILE_MANAGE, "文件管理"),
        MainTabItem(Constants.COMMAND, "命令模式"),
        MainTabItem(Constants.ABOUT, "关于"),
    )

    val choosedTab = remember { mutableIntStateOf(Constants.BAME_INFO) }

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
        SideTabBar(
            deviceMapState.deviceMap,
            deviceMapState.currentChoosedDevice.toString(),
            onDeviceSelect = {
                mainStateHolder.setChooseDevice(it.toInt())
                mainStateHolder.getCurrentDeviceInfo()
            },
            mainItemList,
            onItemClick = { choosedTab.value = it },
            choosePosition = choosedTab.value,
            modifier = Modifier.weight(0.25f)
        )
        DarkDivider(modifier = Modifier.width(2.dp).fillMaxHeight(1f))
        // 右侧内容区
        Box(modifier = Modifier.weight(0.75f)) {
            when (choosedTab.value) {
                Constants.BAME_INFO -> DeviceInfoPage(
                    deviceState,
                    onRefresh = {
                        mainStateHolder.getCurrentDeviceInfo()
                    })

                Constants.INSTALL -> ApkManagePage(appListState) {
                    mainStateHolder.getPackageList(it)
                }

                Constants.FILE_MANAGE -> FileManagePage(
                    DirectoryState(
                        directoryState.deviceCode,
                        directoryState.currentdirectory,
                        directoryState.subdirectories,
                    ),
                    destinationCall = { destination ->
                        mainStateHolder.getFileList(destination)
                    }
                )

                Constants.COMMAND -> CommandPage()

                Constants.PERFORMANCE -> PerformancePage()

                Constants.ABOUT -> AboutPage()
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
    onItemClick: (itemId: Int) -> Unit,
    choosePosition: Int,
    modifier: Modifier
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
        items(mainItemList) {it->
            MainItem(
                title = it.name,
                modifier = modifier.width(160.dp).clickable {
                    onItemClick(it.id)
                },
                isChecked = choosePosition == it.id
            )
        }
    }
}

@Composable
fun MainItem(title: String, modifier: Modifier, isChecked: Boolean) {
    val backGround = if (isChecked) groupBackGroundColor else backGroundColor
    CenterText(
        title,
        style = pageTitleText,
        modifier = modifier
            .background(backGround)
            .clip(RoundedCornerShape(10))
            .padding(vertical = 10.dp)
    )
}
