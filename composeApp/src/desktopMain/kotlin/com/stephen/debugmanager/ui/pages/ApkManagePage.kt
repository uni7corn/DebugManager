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
        }
    }
}