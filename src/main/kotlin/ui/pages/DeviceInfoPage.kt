package ui.pages

import MainStateHolder
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import data.Constants.PULL_FILE_TOAST
import model.uistate.DeviceState
import org.koin.core.context.GlobalContext
import ui.component.*
import ui.theme.alertButtonBackGroundColor
import ui.theme.groupBackGroundColor
import ui.theme.groupTitleText

@Composable
fun DeviceInfoPage(deviceName: DeviceState, onRefresh: () -> Unit) {

    val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

    val toastState = rememberToastState()

    val mockInputSting = remember { mutableStateOf("") }

    val recordTime = remember { mutableStateOf("") }

    BasePage("设备信息") {
        Row {
            LazyColumn(
                modifier = Modifier.fillMaxHeight(1f).weight(0.4f)
                    .clip(RoundedCornerShape(10.dp)).background(groupBackGroundColor)
                    .padding(10.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier.padding(bottom = 10.dp).fillParentMaxWidth(1f),
                    ) {
                        CenterText(
                            "设备基础信息",
                            style = groupTitleText,
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                        CommonButton(
                            onClick = {
                                onRefresh()
                            },
                            text = "刷新设备信息",
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                }
                item {
                    NameValueText("SerialNumber", deviceName.serial ?: "null")
                }
                item {
                    NameValueText("型号", deviceName.name ?: "null")
                }
                item {
                    NameValueText("内部代号", deviceName.innerName ?: "null")
                }
                item {
                    NameValueText("制造商", deviceName.manufacturer ?: "null")
                }
                item {
                    NameValueText("操作系统版本", deviceName.systemVersion ?: "null")
                }
                item {
                    NameValueText("版本构建类型", deviceName.buildType ?: "null")
                }
                item {
                    NameValueText("Android版本", deviceName.sdkVersion ?: "null")
                }
                item {
                    NameValueText("CPU架构", deviceName.cpuArch ?: "null")
                }
                item {
                    NameValueText("分辨率", deviceName.resolution ?: "null")
                }
                item {
                    NameValueText("显示密度", deviceName.density ?: "null")
                }
            }

            LazyColumn(modifier = Modifier.weight(0.6f).padding(start = 10.dp).fillMaxHeight(1f)) {
                item {
                    Column(
                        modifier = Modifier.padding(bottom = 10.dp).clip(RoundedCornerShape(10.dp))
                            .background(groupBackGroundColor)
                            .padding(10.dp)
                    ) {
                        CenterText(
                            "快捷操作", style = groupTitleText, modifier = Modifier.padding(bottom = 10.dp)
                        )
                        Row {
                            CommonButton(
                                "获取ROOT", onClick = {
                                    mainStateHolder.root()
                                },
                                modifier = Modifier.padding(10.dp).weight(1f)
                            )
                            CommonButton(
                                "重载REMOUNT", onClick = {
                                    mainStateHolder.remount()
                                    toastState.show("已执行，如果是刷完机首次remount，请先重启设备")
                                },
                                modifier = Modifier.padding(10.dp).weight(1f)
                            )
                            CommonButton(
                                "Recovery模式", onClick = {
                                    mainStateHolder.rebootRecovery()
                                },
                                modifier = Modifier.padding(10.dp).weight(1f),
                                color = alertButtonBackGroundColor
                            )
                        }
                        Row {
                            CommonButton(
                                "重启设备", onClick = {
                                    mainStateHolder.rebootDevice()
                                },
                                modifier = Modifier.padding(10.dp).weight(1f),
                                color = alertButtonBackGroundColor
                            )
                            CommonButton(
                                "Fastboot模式", onClick = {
                                    mainStateHolder.rebootFastboot()
                                },
                                modifier = Modifier.padding(10.dp).weight(1f),
                                color = alertButtonBackGroundColor
                            )
                            CommonButton(
                                "关机", onClick = {
                                    mainStateHolder.powerOff()
                                },
                                modifier = Modifier.padding(10.dp).weight(1f),
                                color = alertButtonBackGroundColor
                            )
                        }
                        Row {
                            CommonButton(
                                "开始抓取trace", onClick = {
                                    mainStateHolder.startCollectTrace()
                                    toastState.show("默认抓取10s，完成后$PULL_FILE_TOAST")
                                },
                                modifier = Modifier.padding(10.dp).weight(1f)
                            )
                            CommonButton(
                                "打开Google设置", onClick = {
                                    mainStateHolder.openAndroidSettings()
                                },
                                modifier = Modifier.padding(10.dp).weight(1f)
                            )
                        }
                        Row {
                            CommonButton(
                                "打开scrcpy投屏", onClick = {
                                    mainStateHolder.openScreenCopy()
                                },
                                modifier = Modifier.padding(10.dp).weight(1f)
                            )
                        }
                    }
                }
                // 第二个tab
                item {
                    Column(
                        modifier = Modifier.padding(bottom = 10.dp).clip(RoundedCornerShape(10.dp))
                            .background(groupBackGroundColor)
                            .padding(10.dp)
                    ) {
                        CenterText(
                            "录屏与截屏", style = groupTitleText, modifier = Modifier.padding(bottom = 10.dp)
                        )
                        Row {
                            WrappedEditText(
                                value = recordTime.value,
                                tipText = "设置时长(s)",
                                onValueChange = { recordTime.value = it },
                                modifier = Modifier.padding(start = 10.dp, end = 10.dp).weight(1f)
                            )
                            CommonButton(
                                "开始录屏", onClick = {
                                    if (mainStateHolder.isRecording) {
                                        toastState.show("上次录制还未完成")
                                    } else if (recordTime.value.isEmpty()) {
                                        toastState.show("请先输入录制时长")
                                    } else {
                                        kotlin.runCatching {
                                            val timeInt = recordTime.value.toInt()
                                            mainStateHolder.startScreenRecord(timeInt)
                                            recordTime.value = ""
                                            toastState.show("已开始，录制期间会显示手指点击位置，完成后$PULL_FILE_TOAST")
                                        }.onFailure {
                                            toastState.show("请输入正确的时长")
                                            recordTime.value = ""
                                        }
                                    }
                                },
                                modifier = Modifier.padding(10.dp).weight(1f)
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
                                modifier = Modifier.padding(10.dp).weight(1f),
                                color = alertButtonBackGroundColor
                            )
                        }
                        Row {
                            CommonButton(
                                "截屏保存", onClick = {
                                    mainStateHolder.screenshot()
                                    toastState.show(PULL_FILE_TOAST)
                                },
                                modifier = Modifier.padding(10.dp).weight(1f)
                            )
                            CommonButton(
                                "清空截屏图片缓存", onClick = {
                                    mainStateHolder.clearScreenShotsCache()
                                    toastState.show("已清空缓存，节省空间")
                                },
                                modifier = Modifier.padding(10.dp).weight(1f),
                                color = alertButtonBackGroundColor
                            )
                        }
                    }
                }
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(1f).clip(RoundedCornerShape(10.dp))
                            .background(groupBackGroundColor)
                            .padding(10.dp)
                    ) {
                        CenterText(
                            "模拟输入", style = groupTitleText, modifier = Modifier.padding(bottom = 10.dp)
                        )
                        Row {
                            CommonButton(
                                "返回按键", onClick = {
                                    mainStateHolder.mockBackPressed()
                                },
                                modifier = Modifier.padding(10.dp).weight(1f)
                            )
                            CommonButton(
                                "回到桌面", onClick = {
                                    mainStateHolder.mockHomePressed()
                                },
                                modifier = Modifier.padding(10.dp).weight(1f)
                            )
                        }
                        Row {
                            CommonButton(
                                "亮屏", onClick = {
                                    mainStateHolder.turnOnScreen()
                                },
                                modifier = Modifier.padding(10.dp).weight(1f)
                            )
                            CommonButton(
                                "灭屏", onClick = {
                                    mainStateHolder.turnOffScreen()
                                },
                                modifier = Modifier.padding(10.dp).weight(1f)
                            )
                        }
                        Row {
                            CommonButton(
                                "静音开关", onClick = {
                                    mainStateHolder.muteDevice()
                                },
                                modifier = Modifier.padding(10.dp).weight(1f)
                            )
                            CommonButton(
                                "音量+", onClick = {
                                    mainStateHolder.volumeUp()
                                },
                                modifier = Modifier.padding(10.dp).weight(1f)
                            )
                            CommonButton(
                                "音量-", onClick = {
                                    mainStateHolder.volumeDown()
                                },
                                modifier = Modifier.padding(10.dp).weight(1f)
                            )
                        }
                        Row {
                            WrappedEditText(
                                value = mockInputSting.value,
                                tipText = "模拟输入法(English Only)",
                                onValueChange = { mockInputSting.value = it },
                                modifier = Modifier.padding(start = 10.dp, end = 10.dp).weight(1f)
                            )
                            CommonButton(
                                "确认", onClick = {
                                    mainStateHolder.inputText(mockInputSting.value)
                                    mockInputSting.value = ""
                                },
                                modifier = Modifier.padding(10.dp).width(150.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
