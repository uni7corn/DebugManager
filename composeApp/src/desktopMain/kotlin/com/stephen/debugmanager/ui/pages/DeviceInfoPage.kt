package com.stephen.debugmanager.ui.pages

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.stephen.composeapp.generated.resources.Res
import com.stephen.composeapp.generated.resources.device_page_android_version
import com.stephen.composeapp.generated.resources.device_page_back_button
import com.stephen.composeapp.generated.resources.device_page_basic_info
import com.stephen.composeapp.generated.resources.device_page_build_type
import com.stephen.composeapp.generated.resources.device_page_capture_save
import com.stephen.composeapp.generated.resources.device_page_capture_trace
import com.stephen.composeapp.generated.resources.device_page_clear_capture_cache
import com.stephen.composeapp.generated.resources.device_page_clear_record_cache
import com.stephen.composeapp.generated.resources.device_page_confirm
import com.stephen.composeapp.generated.resources.device_page_cpu_arch
import com.stephen.composeapp.generated.resources.device_page_display_id
import com.stephen.composeapp.generated.resources.device_page_dpi
import com.stephen.composeapp.generated.resources.device_page_google_settings
import com.stephen.composeapp.generated.resources.device_page_home_button
import com.stephen.composeapp.generated.resources.device_page_input_text
import com.stephen.composeapp.generated.resources.device_page_internal_code
import com.stephen.composeapp.generated.resources.device_page_lock_screen
import com.stephen.composeapp.generated.resources.device_page_manufacturer
import com.stephen.composeapp.generated.resources.device_page_model_name
import com.stephen.composeapp.generated.resources.device_page_mute_switch
import com.stephen.composeapp.generated.resources.device_page_os_version
import com.stephen.composeapp.generated.resources.device_page_quick_operations
import com.stephen.composeapp.generated.resources.device_page_reboot
import com.stephen.composeapp.generated.resources.device_page_recents_button
import com.stephen.composeapp.generated.resources.device_page_refresh
import com.stephen.composeapp.generated.resources.device_page_remount
import com.stephen.composeapp.generated.resources.device_page_resolution
import com.stephen.composeapp.generated.resources.device_page_root
import com.stephen.composeapp.generated.resources.device_page_scrcpy
import com.stephen.composeapp.generated.resources.device_page_screen_record_capture
import com.stephen.composeapp.generated.resources.device_page_serial_number
import com.stephen.composeapp.generated.resources.device_page_set_duration
import com.stephen.composeapp.generated.resources.device_page_shutdown
import com.stephen.composeapp.generated.resources.device_page_sleep_screen
import com.stephen.composeapp.generated.resources.device_page_start_record
import com.stephen.composeapp.generated.resources.device_page_to_fastboot
import com.stephen.composeapp.generated.resources.device_page_to_recovery
import com.stephen.composeapp.generated.resources.device_page_virtual_buttons
import com.stephen.composeapp.generated.resources.device_page_volume_down
import com.stephen.composeapp.generated.resources.device_page_volume_up
import com.stephen.composeapp.generated.resources.device_page_wake_screen
import com.stephen.composeapp.generated.resources.ic_refresh
import com.stephen.debugmanager.MainStateHolder
import com.stephen.debugmanager.data.Constants.PULL_FILE_TOAST
import com.stephen.debugmanager.data.uistate.DeviceState
import com.stephen.debugmanager.ui.component.*
import com.stephen.debugmanager.ui.theme.groupTitleText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.context.GlobalContext

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DeviceInfoPage(deviceState: DeviceState, onRefresh: () -> Unit) {

    val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

    val scope = rememberCoroutineScope()

    val toastState = rememberToastState()

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val mockInputSting = remember { mutableStateOf("") }

    val recordTime = remember { mutableStateOf("") }

    val displayidString = remember { mutableStateOf("") }

    BasePage({
        Box {
            LazyColumn {
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(
                            modifier = Modifier.animateContentSize()
                                .width(IntrinsicSize.Max)
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
                                CenterText(stringResource(Res.string.device_page_basic_info), style = groupTitleText)
                                Image(
                                    painter = painterResource(Res.drawable.ic_refresh),
                                    contentDescription = stringResource(Res.string.device_page_refresh),
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

                            NameValueText(
                                stringResource(Res.string.device_page_serial_number),
                                deviceState.serial ?: "null"
                            )

                            NameValueText(
                                stringResource(Res.string.device_page_model_name),
                                deviceState.name ?: "null"
                            )

                            NameValueText(
                                stringResource(Res.string.device_page_internal_code),
                                deviceState.innerName ?: "null"
                            )

                            NameValueText(
                                stringResource(Res.string.device_page_manufacturer),
                                deviceState.manufacturer ?: "null"
                            )

                            NameValueText(
                                stringResource(Res.string.device_page_os_version),
                                deviceState.systemVersion ?: "null"
                            )

                            NameValueText(
                                stringResource(Res.string.device_page_build_type),
                                deviceState.buildType ?: "null"
                            )

                            NameValueText(
                                stringResource(Res.string.device_page_android_version),
                                deviceState.sdkVersion ?: "null"
                            )

                            NameValueText(
                                stringResource(Res.string.device_page_cpu_arch),
                                deviceState.cpuArch ?: "null"
                            )

                            NameValueText(
                                stringResource(Res.string.device_page_resolution),
                                deviceState.resolution ?: "null"
                            )

                            NameValueText(
                                stringResource(Res.string.device_page_dpi),
                                deviceState.density ?: "null"
                            )
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
                                stringResource(Res.string.device_page_quick_operations),
                                style = groupTitleText,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                            Row {
                                CommonButton(
                                    stringResource(Res.string.device_page_root), onClick = {
                                        mainStateHolder.root()
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier
                                )
                                CommonButton(
                                    stringResource(Res.string.device_page_remount), onClick = {
                                        mainStateHolder.remount()
                                        toastState.show("已执行，如果是刷完机首次remount，请先重启设备")
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier
                                )
                                CommonButton(
                                    stringResource(Res.string.device_page_to_recovery), onClick = {
                                        mainStateHolder.rebootRecovery()
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier,
                                    btnColor = MaterialTheme.colorScheme.error
                                )
                            }
                            Row {
                                CommonButton(
                                    stringResource(Res.string.device_page_reboot), onClick = {
                                        mainStateHolder.rebootDevice()
                                    },
                                    modifier = itemButtonModifier,
                                    btnColor = MaterialTheme.colorScheme.error,
                                    textModifier = itemButtonTextModifier
                                )
                                CommonButton(
                                    stringResource(Res.string.device_page_to_fastboot), onClick = {
                                        mainStateHolder.rebootFastboot()
                                    },
                                    modifier = itemButtonModifier,
                                    btnColor = MaterialTheme.colorScheme.error,
                                    textModifier = itemButtonTextModifier
                                )
                                CommonButton(
                                    stringResource(Res.string.device_page_shutdown), onClick = {
                                        mainStateHolder.powerOff()
                                    },
                                    modifier = itemButtonModifier,
                                    btnColor = MaterialTheme.colorScheme.error,
                                    textModifier = itemButtonTextModifier
                                )
                            }
                            Row {
                                CommonButton(
                                    stringResource(Res.string.device_page_capture_trace), onClick = {
                                        mainStateHolder.startCollectTrace()
                                        toastState.show("默认抓取10s，$PULL_FILE_TOAST")
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier
                                )
                                CommonButton(
                                    stringResource(Res.string.device_page_google_settings), onClick = {
                                        mainStateHolder.openAndroidSettings()
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier
                                )
                            }
                            Row(
                                modifier = Modifier.padding(start = 10.dp).fillMaxWidth(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                WrappedEditText(
                                    value = displayidString.value,
                                    tipText = stringResource(Res.string.device_page_display_id),
                                    onValueChange = { displayidString.value = it },
                                    maxInputLenth = 1,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.padding(end = 10.dp)
                                        .height(50.dp)
                                        .weight(1f).focusRequester(focusRequester),
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
                                    stringResource(Res.string.device_page_scrcpy), onClick = {
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
                                stringResource(Res.string.device_page_screen_record_capture),
                                style = groupTitleText,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                            Column(modifier = Modifier.width(IntrinsicSize.Max)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    WrappedEditText(
                                        value = recordTime.value,
                                        tipText = stringResource(Res.string.device_page_set_duration),
                                        onValueChange = {
                                            recordTime.value = it
                                        },
                                        maxInputLenth = 1,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.padding(end = 10.dp)
                                            .height(50.dp).weight(1f)
                                            .focusRequester(focusRequester)
                                    )
                                    CommonButton(
                                        stringResource(Res.string.device_page_start_record),
                                        onClick = {
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
                                    stringResource(Res.string.device_page_capture_save), onClick = {
                                        mainStateHolder.screenshot()
                                        toastState.show(PULL_FILE_TOAST)
                                    },
                                    modifier = btnModifier,
                                    textModifier = itemButtonTextModifier
                                )

                                CommonButton(
                                    stringResource(Res.string.device_page_clear_record_cache), onClick = {
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
                                    stringResource(Res.string.device_page_clear_capture_cache), onClick = {
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
                                stringResource(Res.string.device_page_virtual_buttons),
                                style = groupTitleText,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                            Row {
                                CommonButton(
                                    stringResource(Res.string.device_page_back_button), onClick = {
                                        mainStateHolder.mockBackPressed()
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier
                                )
                                CommonButton(
                                    stringResource(Res.string.device_page_home_button), onClick = {
                                        mainStateHolder.mockHomePressed()
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier
                                )
                                CommonButton(
                                    stringResource(Res.string.device_page_recents_button), onClick = {
                                        mainStateHolder.mockRecentPressed()
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier
                                )
                            }
                            Row {
                                CommonButton(
                                    stringResource(Res.string.device_page_wake_screen), onClick = {
                                        mainStateHolder.turnOnScreen()
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier
                                )
                                CommonButton(
                                    stringResource(Res.string.device_page_sleep_screen), onClick = {
                                        mainStateHolder.turnOffScreen()
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier
                                )
                                CommonButton(
                                    stringResource(Res.string.device_page_lock_screen), onClick = {
                                        mainStateHolder.lockScreen()
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier
                                )
                            }
                            Row {
                                CommonButton(
                                    stringResource(Res.string.device_page_mute_switch), onClick = {
                                        mainStateHolder.muteDevice()
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier
                                )
                                CommonButton(
                                    stringResource(Res.string.device_page_volume_up), onClick = {
                                        mainStateHolder.volumeUp()
                                    },
                                    modifier = itemButtonModifier,
                                    textModifier = itemButtonTextModifier
                                )
                                CommonButton(
                                    stringResource(Res.string.device_page_volume_down), onClick = {
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
                                    tipText = stringResource(Res.string.device_page_input_text),
                                    onValueChange = { mockInputSting.value = it },
                                    modifier = Modifier.padding(end = 10.dp)
                                        .weight(1f).focusRequester(focusRequester),
                                    onEnterPressed = {
                                        mainStateHolder.inputText(mockInputSting.value)
                                        mockInputSting.value = ""
                                    }
                                )
                                CommonButton(
                                    stringResource(Res.string.device_page_confirm), onClick = {
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
                focusManager.clearFocus()
                DeviceNoneConnectShade()
            }
        }
    })
}