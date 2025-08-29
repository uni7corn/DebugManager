package com.stephen.debugmanager.ui.pages

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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
import com.stephen.debugmanager.data.bean.PackageInfo
import com.stephen.debugmanager.ui.component.BasePage
import com.stephen.debugmanager.ui.component.CenterText
import com.stephen.debugmanager.ui.component.rememberToastState
import com.stephen.debugmanager.ui.theme.itemKeyText
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.GlobalContext
import java.io.File

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ApkManagePage(
    appListState: List<PackageInfo>,
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
                    )
                }
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
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(5.dp)
            .height(100.dp)
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
            CenterText(text = label)
        }
    }
}