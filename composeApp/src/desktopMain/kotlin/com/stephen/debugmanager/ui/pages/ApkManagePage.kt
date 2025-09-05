package com.stephen.debugmanager.ui.pages

import androidx.compose.animation.*
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.stephen.composeapp.generated.resources.Res
import com.stephen.composeapp.generated.resources.app_manage_page_apk_file_drag_tip
import com.stephen.composeapp.generated.resources.app_manage_page_apk_file_null_tip
import com.stephen.composeapp.generated.resources.app_manage_page_apk_size
import com.stephen.composeapp.generated.resources.app_manage_page_extract_apk
import com.stephen.composeapp.generated.resources.app_manage_page_force_stop
import com.stephen.composeapp.generated.resources.app_manage_page_install_apk
import com.stephen.composeapp.generated.resources.app_manage_page_install_path
import com.stephen.composeapp.generated.resources.app_manage_page_is_system_app
import com.stephen.composeapp.generated.resources.app_manage_page_last_update_time
import com.stephen.composeapp.generated.resources.app_manage_page_min_sdk_version
import com.stephen.composeapp.generated.resources.app_manage_page_show_app_info
import com.stephen.composeapp.generated.resources.app_manage_page_showsystemapp
import com.stephen.composeapp.generated.resources.app_manage_page_start_an_app
import com.stephen.composeapp.generated.resources.app_manage_page_switch_too_fast
import com.stephen.composeapp.generated.resources.app_manage_page_target_sdk_version
import com.stephen.composeapp.generated.resources.device_page_refresh
import com.stephen.composeapp.generated.resources.ic_default_app_icon
import com.stephen.composeapp.generated.resources.ic_refresh
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
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

    val rotateState = remember { mutableStateOf(false) }
    val rotateAnimation by animateFloatAsState(
        targetValue = if (rotateState.value) 720f else 0f,  // 修改为720度
        animationSpec = infiniteRepeatable(
            animation = tween(1000),  // 修改为500毫秒
            repeatMode = RepeatMode.Restart
        )
    )

    val toastState = rememberToastState()

    val scope = rememberCoroutineScope()

    val refreshTooFastTip = stringResource(Res.string.app_manage_page_switch_too_fast)
    val apkFileNullTip = stringResource(Res.string.app_manage_page_apk_file_null_tip)


    BasePage({
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
                                toastState.show(refreshTooFastTip)
                            }
                        },
                        modifier = Modifier.padding(end = 5.dp).size(18.dp)
                    )
                    CenterText(
                        stringResource(Res.string.app_manage_page_showsystemapp),
                        style = infoText,
                        modifier = Modifier.padding(end = 10.dp)
                    )
                    Image(
                        painter = painterResource(Res.drawable.ic_refresh),
                        contentDescription = stringResource(Res.string.device_page_refresh),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .size(20.dp)
                            .rotate(if (rotateState.value) rotateAnimation else 0f)  // 仅在rotateState为true时应用动画
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) {
                                if (!DoubleClickUtils.isFastDoubleClick(3000L)) {
                                    mainStateHolder.getpackageListInfo(selectSystemAppState.value)
                                    scope.launch {
                                        rotateState.value = true
                                        delay(2000)
                                        rotateState.value = false
                                    }
                                } else {
                                    toastState.show(refreshTooFastTip)
                                }
                            }
                    )
                    FileChooseWidget(
                        tintText = stringResource(Res.string.app_manage_page_apk_file_drag_tip),
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
                        installOptions.mapValues { stringResource(it.value) },
                        installParams,
                        modifier = Modifier.padding(end = 5.dp).width(90.dp)
                    ) {
                        installParams = it
                    }
                    CommonButton(
                        text = stringResource(Res.string.app_manage_page_install_apk),
                        onClick = {
                            if (selectedApkFilePathState.value.isNotEmpty()) {
                                mainStateHolder.installApp(selectedApkFilePathState.value, installParams) {
                                    toastState.show(it)
                                }
                            } else {
                                toastState.show(apkFileNullTip)
                            }
                        },
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
                ) {
                    mainStateHolder.copyPathToClipboard(it)
                }
            }
            // 设备未连接，显示提示文案
            if (isDeviceConnected.not()) {
                DeviceNoneConnectShade()
            }
        }
    })
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
    val startApp = stringResource(Res.string.app_manage_page_start_an_app)
    val forceStop = stringResource(Res.string.app_manage_page_force_stop)
    val showInfo = stringResource(Res.string.app_manage_page_show_app_info)
    val extractApk = stringResource(Res.string.app_manage_page_extract_apk)
    ContextMenuArea(items = {
        listOf(
            ContextMenuItem(startApp) {
                onClickOpen()
            },
            ContextMenuItem(forceStop) {
                onForceStop()
            },
            ContextMenuItem(showInfo) {
                onClickShowInfo()
            },
            ContextMenuItem(extractApk) {
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
            NameValueText(
                stringResource(Res.string.app_manage_page_is_system_app),
                if (infoItem?.system == true) "是" else "否",
                nameWeight = 0.3f,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    onClickStringCopy("${infoItem?.packageName}")
                })
            SimpleDivider(Modifier.fillMaxWidth(1f).height(1.dp))
            NameValueText(
                stringResource(Res.string.app_manage_page_min_sdk_version),
                "${infoItem?.minSdkVersion}",
                nameWeight = 0.3f
            )
            SimpleDivider(Modifier.fillMaxWidth(1f).height(1.dp))
            NameValueText(
                stringResource(Res.string.app_manage_page_target_sdk_version),
                "${infoItem?.targetSdkVersion}",
                nameWeight = 0.3f
            )
            SimpleDivider(Modifier.fillMaxWidth(1f).height(1.dp))
            NameValueText(
                stringResource(Res.string.app_manage_page_apk_size),
                "${infoItem?.apkSize?.size()}",
                nameWeight = 0.3f
            )
            SimpleDivider(Modifier.fillMaxWidth(1f).height(1.dp))
            NameValueText(
                stringResource(Res.string.app_manage_page_install_path),
                "${infoItem?.apkPath}", nameWeight = 0.3f,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    onClickStringCopy("${infoItem?.apkPath}")
                })
            SimpleDivider(Modifier.fillMaxWidth(1f).height(1.dp))
            NameValueText(
                stringResource(Res.string.app_manage_page_last_update_time),
                "${infoItem?.lastUpdateTime?.toDateString()}", nameWeight = 0.3f
            )
        }
    }
}