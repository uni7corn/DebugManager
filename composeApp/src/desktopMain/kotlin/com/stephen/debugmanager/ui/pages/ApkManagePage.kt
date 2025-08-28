package com.stephen.debugmanager.ui.pages

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.stephen.composeapp.generated.resources.Res
import com.stephen.composeapp.generated.resources.ic_default_app_icon
import com.stephen.debugmanager.MainStateHolder
import com.stephen.debugmanager.data.InstallParams
import com.stephen.debugmanager.data.PackageFilter
import com.stephen.debugmanager.data.uistate.AppListState
import com.stephen.debugmanager.ui.component.*
import com.stephen.debugmanager.ui.component.skeleton.WeSkeleton
import com.stephen.debugmanager.ui.theme.defaultText
import com.stephen.debugmanager.ui.theme.groupTitleText
import com.stephen.debugmanager.ui.theme.itemKeyText
import com.stephen.debugmanager.utils.DoubleClickUtils
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.GlobalContext
import java.io.File

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ApkManagePage(
    appListState: AppListState,
    isDeviceConnected: Boolean,
    onRefresh: (String) -> Unit
) {

    val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

    val installOptions = mapOf(
        InstallParams.DEFAULT.param to "直接安装",
        InstallParams.REINSTALL.param to "重新安装",
        InstallParams.TEST.param to "测试安装",
        InstallParams.DOWNGRADE.param to "降级安装",
        InstallParams.GRANT.param to "赋权安装"
    )

    var installParams by remember { mutableStateOf(InstallParams.DEFAULT.param) }

    val apkFilter = mapOf(
        PackageFilter.SIMPLE.param to "精简",
        PackageFilter.ALL.param to "全部",
    )

    var apkFilterSelected by remember { mutableStateOf(PackageFilter.SIMPLE.param) }

    var selectedApkFileState = mainStateHolder.selectedApkFileStateFlow.collectAsState()

    val toastState = rememberToastState()

    BasePage("APP安装与管理") {
        Box {
            Column {
                Row(
                    modifier = Modifier.padding(bottom = 10.dp).fillMaxWidth(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface).padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CenterText(
                        "软件安装",
                        style = groupTitleText
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 20.dp).weight(1f)
                    ) {
                        FileChooseWidget(
                            tintText = "拖动 APK 文件到此处 或 点击选取",
                            path = selectedApkFileState.value,
                            modifier = Modifier.weight(3f).padding(end = 10.dp),
                            isChooseFile = true,
                            fileType = "*.apk",
                            onErrorOccur = {
                                toastState.show(it)
                            },
                        ) { path ->
                            mainStateHolder.setSelectedApkFile(path)
                        }
                        DropdownSelector(
                            installOptions,
                            installParams,
                            modifier = Modifier.width(120.dp)
                        ) {
                            installParams = it
                        }
                        CommonButton(
                            text = "安装",
                            onClick = {
                                if (selectedApkFileState.value.isNotEmpty()) {
                                    mainStateHolder.installApp(selectedApkFileState.value, installParams)
                                } else {
                                    toastState.show("请选择一个要安装 apk 文件")
                                }
                            },
                            modifier = Modifier.weight(1f).padding(start = 10.dp),
                            btnColor = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }

                // app大列表
                Column(
                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface).padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CenterText(
                            "APP列表",
                            modifier = Modifier.padding(end = 10.dp),
                            style = groupTitleText
                        )
                        DropdownSelector(
                            apkFilter,
                            apkFilterSelected,
                            modifier = Modifier.width(130.dp)
                        ) {
                            apkFilterSelected = it
                            onRefresh(apkFilterSelected)
                            toastState.show("列表数据量较大，请稍等")
                        }
                    }
                    WeSkeleton.Rectangle(appListState.appMap.isEmpty()) {
//                        LazyColumn {
//                            items(appListState.appMap.keys.toList(), key = { it }) {
//                                Box(
//                                    modifier = Modifier.fillMaxWidth(1f).animateItem()
//                                ) {
//                                    appListState.appMap[it]?.let { item ->
//                                        AppHozItem(
//                                            item.packageName,
//                                            item.appLabel,
//                                            item.version,
//                                            item.iconFilePath,
//                                            item.lastUpdateTime,
//                                        )
//                                    }
//                                }
//                            }
//                        }


                        LazyVerticalGrid(columns = GridCells.Fixed(5)) {
                            items(appListState.appMap.keys.toList(), key = { it }) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(1f).animateItem()
                                ) {
                                    appListState.appMap[it]?.let { item ->
                                        GridAppItem(
                                            item.packageName,
                                            item.appLabel,
                                            item.version,
                                            item.iconFilePath,
                                            item.lastUpdateTime,
                                        )
                                    }
                                }
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
fun AppHozItem(
    packageName: String,
    label: String,
    version: String,
    iconFilePath: String,
    lastUpdateTime: String,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(1f).padding(vertical = 5.dp)
            .border(2.dp, MaterialTheme.colorScheme.onSecondary, RoundedCornerShape(10.dp))
            .padding(5.dp)
    ) {
        val imageState = remember { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty) }
        Box {
            AsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(File(iconFilePath))
                    .build(),
                modifier = Modifier.padding(start = 5.dp).size(50.dp),
                contentDescription = "app icon",
                onState = {
                    imageState.value = it
                }
            )
            // 错误和加载中，都显示默认值
            val isNeedShowLoading =
                imageState.value is AsyncImagePainter.State.Error || imageState.value is AsyncImagePainter.State.Loading
            val infiniteTransition = rememberInfiniteTransition()
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing)
                )
            )
            if (isNeedShowLoading) {
                Image(
                    painter = painterResource(Res.drawable.ic_default_app_icon),
                    contentDescription = "loading icon",
                    modifier = Modifier.size(50.dp).padding(start = 5.dp).rotate(rotation)
                )
            }
        }
        Column(modifier = Modifier.padding(start = 10.dp).weight(0.4f)) {
            CenterText(text = label, style = itemKeyText)
            CenterText(text = version, style = defaultText)
            SelectionContainer {
                CenterText(text = packageName, style = defaultText)
            }
        }

        CenterText(
            text = "上次更新时间:$lastUpdateTime",
            style = defaultText,
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Composable
fun GridAppItem(
    packageName: String,
    label: String,
    version: String,
    iconFilePath: String,
    lastUpdateTime: String,
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(5.dp)
            .border(2.dp, MaterialTheme.colorScheme.onSecondary, RoundedCornerShape(10.dp))
            .padding(5.dp)
    ) {
        val imageState = remember { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty) }
        Box {
            AsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(File(iconFilePath))
                    .build(),
                modifier = Modifier.padding(start = 5.dp).size(50.dp),
                contentDescription = "app icon",
                onState = {
                    imageState.value = it
                }
            )
            // 错误和加载中，都显示默认值
            val isNeedShowLoading =
                imageState.value is AsyncImagePainter.State.Error || imageState.value is AsyncImagePainter.State.Loading
            val infiniteTransition = rememberInfiniteTransition()
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing)
                )
            )
            if (isNeedShowLoading) {
                Image(
                    painter = painterResource(Res.drawable.ic_default_app_icon),
                    contentDescription = "loading icon",
                    modifier = Modifier.size(50.dp).padding(start = 5.dp).rotate(rotation)
                )
            }
        }
        Column(modifier = Modifier.padding(start = 10.dp).weight(0.4f)) {
            CenterText(text = label, style = itemKeyText)
        }
    }
}