package com.stephen.debugmanager.ui.pages

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.stephen.composeapp.generated.resources.Res
import com.stephen.composeapp.generated.resources.ic_refresh
import com.stephen.debugmanager.MainStateHolder
import com.stephen.debugmanager.data.Constants.PULL_FILE_TOAST
import com.stephen.debugmanager.data.uistate.DeviceState
import com.stephen.debugmanager.ui.component.*
import com.stephen.debugmanager.ui.theme.groupTitleText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.GlobalContext

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DeviceInfoPage(deviceState: DeviceState, onRefresh: () -> Unit) {

    val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

    val scope = rememberCoroutineScope()

    val toastState = rememberToastState()

    val mockInputSting = remember { mutableStateOf("") }

    val recordTime = remember { mutableStateOf("") }

    val displayidString = remember { mutableStateOf("") }

    BasePage("设备信息") {
        Box {
            LazyColumn {
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(
                            modifier = Modifier.width(IntrinsicSize.Max)
                                .padding(vertical = 5.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(1f).padding(bottom = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val rotateState = remember { mutableStateOf(false) }
                                val rotateAnimation by animateFloatAsState(
                                    targetValue = if (rotateState.value) 720f else 0f,  // 修改为720度
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(1000),  // 修改为500毫秒
                                        repeatMode = RepeatMode.Restart
                                    )
                                )
                                CenterText("设备基础信息", style = groupTitleText)
                                Image(
                                    painter = painterResource(Res.drawable.ic_refresh),
                                    contentDescription = "刷新",
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                                    modifier = Modifier
                                        .padding(start = 10.dp)
                                        .size(20.dp)
                                        .rotate(if (rotateState.value) rotateAnimation else 0f)  // 仅在rotateState为true时应用动画
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                        ) {
                                            onRefresh()
                                            scope.launch {
                                                rotateState.value = true
                                                delay(2000)
                                                rotateState.value = false
                                            }
                                        }
                                )
                            }

                            NameValueText("SerialNumber", deviceState.serial ?: "null")

                            NameValueText("型号", deviceState.name ?: "null")

                            NameValueText("内部代号", deviceState.innerName ?: "null")

                            NameValueText("制造商", deviceState.manufacturer ?: "null")

                            NameValueText("操作系统版本", deviceState.systemVersion ?: "null")

                            NameValueText("版本构建类型", deviceState.buildType ?: "null")

                            NameValueText("Android版本", deviceState.sdkVersion ?: "null")

                            NameValueText("CPU架构", deviceState.cpuArch ?: "null")

                            NameValueText("分辨率", deviceState.resolution ?: "null")

                            NameValueText("显示密度", deviceState.density ?: "null")
                        }

                        // 功能按钮共用描述符
                        val itemButtonModifier = Modifier
                            .weight(1f)
                            .padding(5.dp)

                        val itemButtonTextModifier = Modifier.padding(vertical = 5.dp)

                        Column(
                            modifier = Modifier
                                .width(IntrinsicSize.Max)
                                .fillMaxRowHeight()
                                .padding(vertical = 5.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(10.dp)
                        ) {
                            CenterText(
                                "快捷操作",
                                style = groupTitleText,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                            Row {
                                CommonButton(
                                    "ROOT", onClick = {
                                        mainStateHolder.root()
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier
                                )
                                CommonButton(
                                    "REMOUNT", onClick = {
                                        mainStateHolder.remount()
                                        toastState.show("已执行，如果是刷完机首次remount，请先重启设备")
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier
                                )
                                CommonButton(
                                    "To Recovery", onClick = {
                                        mainStateHolder.rebootRecovery()
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier,
                                    btnColor = MaterialTheme.colorScheme.error
                                )
                            }
                            Row {
                                CommonButton(
                                    "REBOOT", onClick = {
                                        mainStateHolder.rebootDevice()
                                    },
                                    modifier = itemButtonModifier,
                                    btnColor = MaterialTheme.colorScheme.error,
                                    textModifier = itemButtonTextModifier
                                )
                                CommonButton(
                                    "To Fastboot", onClick = {
                                        mainStateHolder.rebootFastboot()
                                    },
                                    modifier = itemButtonModifier,
                                    btnColor = MaterialTheme.colorScheme.error,
                                    textModifier = itemButtonTextModifier
                                )
                                CommonButton(
                                    "关机", onClick = {
                                        mainStateHolder.powerOff()
                                    },
                                    modifier = itemButtonModifier,
                                    btnColor = MaterialTheme.colorScheme.error,
                                    textModifier = itemButtonTextModifier
                                )
                            }
                            Row {
                                CommonButton(
                                    "抓取Trace", onClick = {
                                        mainStateHolder.startCollectTrace()
                                        toastState.show("默认抓取10s，$PULL_FILE_TOAST")
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier
                                )
                                CommonButton(
                                    "Google设置", onClick = {
                                        mainStateHolder.openAndroidSettings()
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                WrappedEditText(
                                    value = displayidString.value,
                                    tipText = "输入DisplayId(默认为0)",
                                    onValueChange = { displayidString.value = it },
                                    modifier = Modifier.padding(start = 10.dp, end = 10.dp)
                                        .weight(1f),
                                    onEnterPressed = {
                                        if (displayidString.value.isEmpty()) {
                                            toastState.show("参数为空，默认设置displayid为0")
                                            mainStateHolder.openScrcpyById()
                                        } else {
                                            mainStateHolder.openScrcpyById(displayidString.value)
                                            displayidString.value = ""
                                        }
                                    }
                                )
                                CommonButton(
                                    "Scrcpy投屏", onClick = {
                                        if (displayidString.value.isEmpty()) {
                                            toastState.show("参数为空，默认设置displayid为0")
                                            mainStateHolder.openScrcpyById()
                                        } else {
                                            mainStateHolder.openScrcpyById(displayidString.value)
                                            displayidString.value = ""
                                        }
                                    },
                                    modifier = Modifier.padding(10.dp),
                                    textModifier = itemButtonTextModifier,
                                    btnColor = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .width(IntrinsicSize.Min)
                                .fillMaxRowHeight()
                                .padding(vertical = 5.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(10.dp)
                        ) {
                            CenterText(
                                "录屏与截屏",
                                style = groupTitleText,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                            Column(modifier = Modifier.width(IntrinsicSize.Max)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    WrappedEditText(
                                        value = recordTime.value,
                                        tipText = "设置时长(s)",
                                        onValueChange = {
                                            recordTime.value = it
                                        },
                                        modifier = Modifier.padding(horizontal = 5.dp).weight(1f)
                                    )
                                    CommonButton(
                                        "开始录屏", onClick = {
                                            if (mainStateHolder.isRecording) {
                                                toastState.show("上次录制还未完成")
                                            } else if (recordTime.value.isEmpty()) {
                                                toastState.show("请先输入录制时长")
                                            } else {
                                                runCatching {
                                                    val timeInt = recordTime.value.toInt()
                                                    mainStateHolder.startScreenRecord(timeInt)
                                                    recordTime.value = ""
                                                    toastState.show("已开始，录制期间会显示手指点击位置，$PULL_FILE_TOAST")
                                                }.onFailure {
                                                    toastState.show("请输入正确的时长")
                                                    recordTime.value = ""
                                                }
                                            }
                                        },
                                        modifier = Modifier.padding(10.dp),
                                        textModifier = itemButtonTextModifier,
                                        btnColor = MaterialTheme.colorScheme.tertiary
                                    )
                                }

                                val btnModifier = Modifier
                                    .fillMaxWidth(1f)
                                    .padding(vertical = 5.dp)

                                CommonButton(
                                    "截屏保存", onClick = {
                                        mainStateHolder.screenshot()
                                        toastState.show(PULL_FILE_TOAST)
                                    },
                                    modifier = btnModifier,
                                    textModifier = itemButtonTextModifier
                                )

                                CommonButton(
                                    "清空录屏缓存", onClick = {
                                        if (mainStateHolder.isRecording) {
                                            toastState.show("录屏中，请稍后再试")
                                        } else {
                                            mainStateHolder.clearRecordCache()
                                            toastState.show("已清空缓存，节省空间")
                                        }
                                    },
                                    modifier = btnModifier,
                                    btnColor = MaterialTheme.colorScheme.error,
                                    textModifier = itemButtonTextModifier
                                )

                                CommonButton(
                                    "清空截屏图片缓存", onClick = {
                                        mainStateHolder.clearScreenShotsCache()
                                        toastState.show("已清空缓存，节省空间")
                                    },
                                    modifier = btnModifier,
                                    btnColor = MaterialTheme.colorScheme.error,
                                    textModifier = itemButtonTextModifier
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .width(IntrinsicSize.Min)
                                .fillMaxRowHeight()
                                .padding(vertical = 5.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(10.dp)
                        ) {
                            CenterText(
                                "模拟按键",
                                style = groupTitleText,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                            Row {
                                CommonButton(
                                    "返回按键", onClick = {
                                        mainStateHolder.mockBackPressed()
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier
                                )
                                CommonButton(
                                    "回到桌面", onClick = {
                                        mainStateHolder.mockHomePressed()
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier
                                )
                                CommonButton(
                                    "最近任务", onClick = {
                                        mainStateHolder.mockRecentPressed()
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier
                                )
                            }
                            Row {
                                CommonButton(
                                    "亮屏", onClick = {
                                        mainStateHolder.turnOnScreen()
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier
                                )
                                CommonButton(
                                    "灭屏", onClick = {
                                        mainStateHolder.turnOffScreen()
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier
                                )
                                CommonButton(
                                    "锁屏", onClick = {
                                        mainStateHolder.lockScreen()
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier
                                )
                            }
                            Row {
                                CommonButton(
                                    "静音开关", onClick = {
                                        mainStateHolder.muteDevice()
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier
                                )
                                CommonButton(
                                    "音量+", onClick = {
                                        mainStateHolder.volumeUp()
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier
                                )
                                CommonButton(
                                    "音量-", onClick = {
                                        mainStateHolder.volumeDown()
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(1f).padding(5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                WrappedEditText(
                                    value = mockInputSting.value,
                                    tipText = "模拟输入法(English Only)",
                                    onValueChange = { mockInputSting.value = it },
                                    modifier = Modifier.padding(start = 10.dp, end = 10.dp)
                                        .weight(1f),
                                    onEnterPressed = {
                                        mainStateHolder.inputText(mockInputSting.value)
                                        mockInputSting.value = ""
                                    }
                                )
                                CommonButton(
                                    "确认", onClick = {
                                        mainStateHolder.inputText(mockInputSting.value)
                                        mockInputSting.value = ""
                                    },
                                    modifier = Modifier.padding(10.dp),
                                    textModifier = itemButtonTextModifier,
                                    btnColor = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                }
            }
            // 设备未连接，显示提示文案
            if (deviceState.isConnected.not()) {
                DeviceNoneConnectShade()
            }
        }
    }
}