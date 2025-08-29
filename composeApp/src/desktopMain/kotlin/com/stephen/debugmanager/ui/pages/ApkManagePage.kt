package com.stephen.debugmanager.ui.pages

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.stephen.debugmanager.ui.theme.infoText
import com.stephen.debugmanager.utils.DoubleClickUtils
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
                            if (!DoubleClickUtils.isFastDoubleClick(2000L)) {
                                mainStateHolder.setSelectSystemApp(it)
                                onChangeSystemAppShowState(it)
                            } else {
                                toastState.show("切换太快了~")
                            }
                        },
                        modifier = Modifier.padding(end = 5.dp).size(20.dp)
                    )
                    CenterText("显示系统APP", modifier = Modifier.padding(end = 16.dp))
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
                                mainStateHolder.installApp(selectedApkFilePathState.value, installParams)
                            } else {
                                toastState.show("请选择一个要安装 apk 文件")
                            }
                        },
                        modifier = Modifier.padding(start = 10.dp).padding(horizontal = 10.dp),
                        btnColor = MaterialTheme.colorScheme.tertiary
                    )
                }

                LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 120.dp)) {
                    items(appListState.sortedBy { it.label }, key = { it.packageName }) {
                        Box(
                            Modifier.animateItem(
                                fadeInSpec = null,
                                fadeOutSpec = null,
                                placementSpec = tween(300)
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            GridAppItem(
                                it.packageName,
                                it.label,
                                it.versionName,
                                mainStateHolder.getIconFilePath(it.packageName),
                                it.lastUpdateTime.toString(),
                                modifier = Modifier.padding(5.dp).padding(5.dp)
                            )
                        }
                    }
                }
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
    packageName: String,
    label: String,
    version: String,
    iconFilePath: String,
    lastUpdateTime: String,
    modifier: Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clip(RoundedCornerShape(10))
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