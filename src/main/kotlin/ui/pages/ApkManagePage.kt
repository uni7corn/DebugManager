package ui.pages

import DropdownSelector
import MainStateHolder
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import data.Constants.PULL_FILE_TOAST
import data.InstallParams
import data.PackageFilter
import model.uistate.AppListState
import org.koin.core.context.GlobalContext
import ui.component.*
import ui.component.skeleton.WeSkeleton
import ui.theme.*
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

@Composable
fun ApkManagePage(appListState: AppListState, onRefresh: (String) -> Unit) {

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

    var selectedFile by remember { mutableStateOf<File?>(null) }

    val toastState = rememberToastState()

    BasePage("APP安装与管理") {
        Row(
            modifier = Modifier.padding(bottom = 10.dp)
                .clip(RoundedCornerShape(10.dp)).fillMaxWidth(1f)
                .background(groupBackGroundColor).padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CenterText(
                "软件安装",
                style = groupTitleText
            )
            CenterText(
                text = "选择 apk 路径: ${selectedFile?.absolutePath}",
                style = defaultText,
                modifier = Modifier.padding(start = 20.dp).weight(1f)
                    .border(2.dp, fontPrimaryColor, RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .background(locationBackColor).clickable {
                        val fileChooser = FileDialog(
                            Frame(),
                            "Select a file",
                            FileDialog.LOAD
                        ).apply {
                            file = "*.apk"
                        }
                        fileChooser.isVisible = true
                        selectedFile = fileChooser.directory?.let { File(it, fileChooser.file) }
                    }.padding(10.dp)
            )
            Row(
                modifier = Modifier.weight(0.5f).padding(start = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DropdownSelector(
                    installOptions,
                    installParams,
                    modifier = Modifier.width(130.dp)
                ) {
                    installParams = it
                }
                Spacer(modifier = Modifier.weight(1f))
                CommonButton(
                    text = "安装",
                    onClick = {
                        selectedFile?.absolutePath?.let {
                            mainStateHolder.installApp(it, installParams)
                        } ?: run {
                            toastState.show("请选择一个要安装 apk 文件")
                        }
                    },
                    modifier = Modifier.padding(end = 10.dp)
                )
            }
        }

        // app大列表
        Column(
            modifier = Modifier.clip(RoundedCornerShape(10.dp))
                .background(groupBackGroundColor).padding(10.dp)
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
            WeSkeleton.Rectangle(appListState.appList.isEmpty()) {
                LazyColumn {
                    items(appListState.appList) {
                        Box(
                            modifier = Modifier.fillMaxWidth(1f)
                        ) {
                            AppItem(
                                it.packageName,
                                it.appLabel,
                                it.version,
                                it.icon,
                                it.lastUpdateTime,
                                toastState = toastState
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppItem(
    packageName: String,
    label: String,
    version: String,
    iconBitmap: ImageBitmap,
    lastUpdateTime: String,
    toastState: ToastState
) {

    val optionsDialogState = remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(1f).padding(vertical = 5.dp)
            .border(2.dp, fontPrimaryColor, RoundedCornerShape(10.dp)).padding(5.dp)
    ) {
        Image(
            painter = BitmapPainter(image = iconBitmap),
            modifier = Modifier.padding(start = 5.dp).size(50.dp),
            contentDescription = "app icon"
        )

        Column(modifier = Modifier.padding(start = 10.dp).weight(0.4f)) {
            CenterText(text = label, style = itemKeyText)
            CenterText(text = version, style = defaultText)
            CenterText(text = packageName, style = defaultText)
        }

        CenterText(text = "上次更新时间:$lastUpdateTime", style = defaultText, modifier = Modifier.weight(0.6f))

        Image(
            contentDescription = "app options",
            painter = painterResource("image/ic_options.png"),
            modifier = Modifier.padding(end = 10.dp)
                .clip(RoundedCornerShape(10.dp)).clickable {
                    optionsDialogState.value = true
                }.size(36.dp).padding(5.dp)
        )

        if (optionsDialogState.value) {
            OptionsDialog(label, packageName, toastState = toastState) {
                optionsDialogState.value = false
            }
        }
    }
}

@Composable
fun OptionsDialog(label: String, packageName: String, toastState: ToastState, dismiss: () -> Unit) {

    val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

    var selectedPushApk by remember { mutableStateOf<File?>(null) }

    Dialog(
        // 点外面不让消除，防止双击apk文件时易误消除
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier.width(380.dp).clip(RoundedCornerShape(10.dp))
                .background(dialogBackColor),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource("image/ic_dialog_close.png"),
                    contentDescription = "close",
                    modifier = Modifier.size(28.dp).padding(end = 10.dp, top = 10.dp).clickable {
                        dismiss()
                    }
                )
            }
            CenterText(
                "请选择要对${label}进行的操作",
                groupTitleText,
                modifier = Modifier.padding(10.dp).fillMaxWidth(1f)
            )
            CommonButton(
                "打开应用",
                onClick = {
                    toastState.show("部分服务类应用无主界面，打开操作可能无效")
                    mainStateHolder.startMainActivity(packageName)
                    dismiss()
                },
                modifier = Modifier.height(45.dp).padding(5.dp).fillMaxWidth(1f)
            )
            CommonButton(
                "卸载", onClick = {
                    toastState.show("注意系统预制应用可能卸载无效")
                    mainStateHolder.uninstallApp(packageName)
                    dismiss()
                },
                modifier = Modifier.height(45.dp).padding(5.dp).fillMaxWidth(1f),
                color = alertButtonBackGroundColor
            )
            CommonButton(
                "提取APK",
                onClick = {
                    toastState.show(PULL_FILE_TOAST)
                    mainStateHolder.pullInstalledApk(packageName)
                    dismiss()
                },
                modifier = Modifier.height(45.dp).padding(5.dp).fillMaxWidth(1f)
            )
            LightDivider(Modifier.height(2.dp).fillMaxWidth(1f))
            CenterText(
                "置换apk",
                defaultText,
                modifier = Modifier.fillMaxWidth(1f).padding(top = 10.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(1f).padding(10.dp)
            ) {
                CenterText(
                    text = "选择 apk 路径: ${selectedPushApk?.absolutePath}",
                    style = defaultText,
                    modifier = Modifier.weight(1f)
                        .border(2.dp, fontPrimaryColor, RoundedCornerShape(10.dp))
                        .clip(RoundedCornerShape(10.dp))
                        .background(locationBackColor).clickable {
                            val fileChooser = FileDialog(
                                Frame(),
                                "Select a file",
                                FileDialog.LOAD
                            ).apply {
                                file = "*.apk"
                            }
                            fileChooser.isVisible = true
                            selectedPushApk = fileChooser.directory?.let { File(it, fileChooser.file) }
                        }.padding(vertical = 10.dp, horizontal = 5.dp)
                )
                CommonButton(
                    "PUSH",
                    onClick = {
                        selectedPushApk?.absolutePath?.let {
                            toastState.show("自动替换推送中，稍后重启即可")
                            mainStateHolder.pushApk(packageName, it)
                        } ?: run {
                            toastState.show("请选择一个要替换的 apk 文件")
                        }
                        dismiss()
                    },
                    modifier = Modifier.height(36.dp).padding(start = 10.dp),
                )
            }
        }
    }
}