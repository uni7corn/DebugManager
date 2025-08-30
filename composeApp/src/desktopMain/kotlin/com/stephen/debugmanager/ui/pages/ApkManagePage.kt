package com.stephen.debugmanager.ui.pages

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.stephen.composeapp.generated.resources.Res
import com.stephen.composeapp.generated.resources.ic_default_app_icon
import com.stephen.debugmanager.MainStateHolder
import com.stephen.debugmanager.data.InstallParams
import com.stephen.debugmanager.data.bean.PackageInfo
import com.stephen.debugmanager.data.installOptions
import com.stephen.debugmanager.ui.component.*
import com.stephen.debugmanager.ui.theme.groupTitleText
import com.stephen.debugmanager.ui.theme.infoText
import com.stephen.debugmanager.utils.DoubleClickUtils
import com.stephen.debugmanager.utils.size
import com.stephen.debugmanager.utils.toDateString
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.GlobalContext
import java.io.File

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ApkManagePage(
    appListState: List<PackageInfo>,
    isDeviceConnected: Boolean,
    onChangeSystemAppShowState: (Boolean) -> Unit
) {

    val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

    val selectSystemAppState = mainStateHolder.selectSystemAppState

    val dialogInfoItem = remember { mutableStateOf<PackageInfo?>(null) }

    val selectedApkFilePathState = mainStateHolder.selectedApkFilePathStateFlow.collectAsState()

    var installParams by remember { mutableStateOf(InstallParams.DEFAULT.param) }

    val toastState = rememberToastState()

    BasePage("APP安装与管理") {
        Box {
            Column {
                // 功能按钮，删除，刷新等等
                Row(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(10))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selectSystemAppState.value, onCheckedChange = {
                            if (!DoubleClickUtils.isFastDoubleClick(3000L)) {
                                mainStateHolder.setSelectSystemApp(it)
                                onChangeSystemAppShowState(it)
                            } else {
                                toastState.show("切换太快了~")
                            }
                        },
                        modifier = Modifier.padding(end = 5.dp).size(18.dp)
                    )
                    CenterText("显示系统APP", style = infoText, modifier = Modifier.padding(end = 16.dp))
                    FileChooseWidget(
                        tintText = "拖动 APK 文件到此处 或 点击选取",
                        path = selectedApkFilePathState.value,
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
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
                        modifier = Modifier.width(100.dp)
                    ) {
                        installParams = it
                    }
                    CommonButton(
                        text = "安装",
                        onClick = {
                            if (selectedApkFilePathState.value.isNotEmpty()) {
                                mainStateHolder.installApp(selectedApkFilePathState.value, installParams) {
                                    toastState.show(it)
                                }
                            } else {
                                toastState.show("请选择一个要安装 apk 文件")
                            }
                        },
                        modifier = Modifier.padding(start = 10.dp).padding(horizontal = 10.dp),
                        btnColor = MaterialTheme.colorScheme.tertiary
                    )
                }

                LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 105.dp)) {
                    items(appListState.sortedBy { it.label }, key = { it.packageName }) {
                        Box(
                            Modifier.animateItem(
                                fadeInSpec = null,
                                fadeOutSpec = null,
                                placementSpec = tween(300)
                            )
                        ) {
                            GridAppItem(
                                label = it.label,
                                iconFilePath = mainStateHolder.getIconFilePath(it.packageName),
                                modifier = Modifier.padding(5.dp)
                                    .size(100.dp).clip(RoundedCornerShape(10))
                                    .padding(5.dp)
                                    .bounceClick().clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                        dialogInfoItem.value = it
                                    },
                                onClickShowInfo = {
                                    dialogInfoItem.value = it
                                },
                                onClickOpen = {
                                    mainStateHolder.startMainActivity(it.packageName)
                                },
                                onForceStop = {
                                    mainStateHolder.forceStopApp(it.packageName)
                                },
                                onExtractApk = {
                                    mainStateHolder.pullInstalledApk(it.packageName, it.versionName)
                                },
                            )
                        }
                    }
                }
            }
            AnimatedVisibility(
                visible = dialogInfoItem.value != null,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut(),
                modifier = Modifier.fillMaxSize(1f)
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f))
                    .clickable(indication = null, interactionSource = remember {
                        MutableInteractionSource()
                    }) {
                        dialogInfoItem.value = null
                    }
            ) {
                AppInfoDialog(
                    dialogInfoItem.value,
                    mainStateHolder.getIconFilePath(dialogInfoItem.value?.packageName ?: "")
                )
            }
            // 设备未连接，显示提示文案
            if (isDeviceConnected.not()) {
                DeviceNoneConnectShade()
            }
        }
    }
}

@Composable
fun GridAppItem(
    label: String,
    iconFilePath: String,
    modifier: Modifier,
    onClickShowInfo: () -> Unit,
    onClickOpen: () -> Unit,
    onForceStop: () -> Unit,
    onExtractApk: () -> Unit,
) {
    ContextMenuArea(items = {
        listOf(
            ContextMenuItem("打开") {
                onClickOpen()
            },
            ContextMenuItem("强制停止") {
                onForceStop()
            },
            ContextMenuItem("展示信息") {
                onClickShowInfo()
            },
            ContextMenuItem("提取安装包") {
                onExtractApk()
            },
        )
    }) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val imageState = remember { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty) }
                Box {
                    // 错误和加载中，都显示默认值
                    val isNeedShowLoading =
                        imageState.value is AsyncImagePainter.State.Error || imageState.value is AsyncImagePainter.State.Loading
                    if (isNeedShowLoading) {
                        Image(
                            painter = painterResource(Res.drawable.ic_default_app_icon),
                            contentDescription = "loading icon",
                            modifier = Modifier.size(50.dp).padding(start = 5.dp)
                        )
                    }
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
                }

                CenterText(
                    label,
                    modifier = Modifier.padding(6.dp),
                    isNeedToClipText = true,
                    style = infoText,
                )
            }
        }
    }
}

@Composable
fun AppInfoDialog(infoItem: PackageInfo?, iconFilePath: String, onClickStringCopy: (String) -> Unit = {}) {
    Box(contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.width(400.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {}
                .padding(vertical = 5.dp, horizontal = 10.dp)
        ) {
            val imageState = remember { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty) }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 5.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalPlatformContext.current)
                        .data(File(iconFilePath))
                        .build(),
                    modifier = Modifier.padding(start = 5.dp, top = 5.dp, bottom = 5.dp, end = 10.dp).size(60.dp),
                    contentDescription = "app icon",
                    onState = {
                        imageState.value = it
                    }
                )
                Column {
                    CenterText(
                        "${infoItem?.label}",
                        style = groupTitleText,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                    CenterText(
                        "${infoItem?.versionName}",
                        isNeedToClipText = true,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                    CenterText(
                        "${infoItem?.packageName}",
                        isNeedToClipText = true,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
            SimpleDivider(Modifier.fillMaxWidth(1f).height(1.dp))
            NameValueText("是否系统APP", if (infoItem?.system == true) "是" else "否", nameWeight = 0.3f)
            SimpleDivider(Modifier.fillMaxWidth(1f).height(1.dp))
            NameValueText("最小SDK版本", "${infoItem?.minSdkVersion}", nameWeight = 0.3f)
            SimpleDivider(Modifier.fillMaxWidth(1f).height(1.dp))
            NameValueText("目标SDK版本", "${infoItem?.targetSdkVersion}", nameWeight = 0.3f)
            SimpleDivider(Modifier.fillMaxWidth(1f).height(1.dp))
            NameValueText("安装包大小", "${infoItem?.apkSize?.size()}", nameWeight = 0.3f)
            SimpleDivider(Modifier.fillMaxWidth(1f).height(1.dp))
            NameValueText("安装路径", "${infoItem?.apkPath}", nameWeight = 0.3f)
            SimpleDivider(Modifier.fillMaxWidth(1f).height(1.dp))
            NameValueText("最后更新时间", "${infoItem?.lastUpdateTime?.toDateString()}", nameWeight = 0.3f)
        }
    }
}