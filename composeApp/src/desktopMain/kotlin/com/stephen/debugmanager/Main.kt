package com.stephen.debugmanager

import androidx.compose.foundation.DarkDefaultContextMenuRepresentation
import androidx.compose.foundation.LightDefaultContextMenuRepresentation
import androidx.compose.foundation.LocalContextMenuRepresentation
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberNotification
import androidx.compose.ui.window.rememberTrayState
import androidx.compose.ui.window.rememberWindowState
import com.stephen.composeapp.generated.resources.Res
import com.stephen.composeapp.generated.resources.app_logo
import com.stephen.debugmanager.base.PlatformAdapter
import com.stephen.debugmanager.data.LanguageState
import com.stephen.debugmanager.data.ThemeState
import com.stephen.debugmanager.di.koinModules
import com.stephen.debugmanager.ui.ContentView
import com.stephen.debugmanager.ui.SingleProcessTipWindow
import com.stephen.debugmanager.ui.component.AnimatedTheme
import com.stephen.debugmanager.ui.component.CommonDialog
import com.stephen.debugmanager.ui.component.CustomTitleBar
import com.stephen.debugmanager.ui.pages.SplashScreen
import com.stephen.debugmanager.ui.theme.DarkColorScheme
import com.stephen.debugmanager.ui.theme.LightColorScheme
import com.stephen.debugmanager.utils.LogUtils
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import java.awt.Dimension
import java.util.Locale

fun main() = application {

    startKoin {
        modules(koinModules)
    }

    val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

    val windowState = rememberWindowState(width = 1000.dp, height = 650.dp)

    val dialogState = remember { mutableStateOf(false) }

    val themeState = mainStateHolder.themeStateStateFlow.collectAsState()

    val languageState = mainStateHolder.languageStateStateFlow.collectAsState()

    val isMenuExpanded = remember { mutableStateOf(true) }

    val isOtherInstanceRunning = mainStateHolder.isOtherInstanceRunning

    val trayState = rememberTrayState()

    val notification = rememberNotification("Notification", "Message from MyApp!")

    if (isOtherInstanceRunning.value) {
        SingleProcessTipWindow()
    } else {
        Tray(
            state = trayState,
            icon = painterResource(Res.drawable.app_logo),
            tooltip = "DebugManager",
            onAction = {
                windowState.isMinimized = false
            },
            menu = {
                Item("打开主界面", onClick = {
                    windowState.isMinimized = false
                })
                Item("发通知测试", onClick = {
                    trayState.sendNotification(notification)
                })
                Item("退出应用", onClick = {
                    exitApplication()
                })
            }
        )

        Window(
            onCloseRequest = {
                if (windowState.isMinimized)
                    windowState.isMinimized = false
                dialogState.value = true
            },
            transparent = true,
            title = "DebugManager",
            undecorated = true,
            state = windowState,
            icon = painterResource(Res.drawable.app_logo),
        ) {
            // set the minimum size
            window.minimumSize = Dimension(600, 650)

            AnimatedTheme(
                targetColorScheme = when (themeState.value) {
                    ThemeState.DARK -> DarkColorScheme
                    ThemeState.LIGHT -> LightColorScheme
                    else -> if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
                }
            ) {
                val contextMenuRepresentation = when (themeState.value) {
                    ThemeState.DARK -> DarkDefaultContextMenuRepresentation
                    ThemeState.LIGHT -> LightDefaultContextMenuRepresentation
                    else -> if (isSystemInDarkTheme()) DarkDefaultContextMenuRepresentation
                    else LightDefaultContextMenuRepresentation
                }

                val LocalLocalization = staticCompositionLocalOf { "en" }
                var languageCode by remember { mutableStateOf("en") }

                LaunchedEffect(languageState.value) {
                    LogUtils.printLog("languageState.value: ${languageState.value}")
                    val locale = when (languageState.value) {
                        LanguageState.CHINESE -> Locale("zh", "CN")
                        LanguageState.ENGLISH -> Locale("en", "US")
                        else -> PlatformAdapter.systemLocale
                    }
                    Locale.setDefault(locale)
                    languageCode = when (languageState.value) {
                        LanguageState.CHINESE -> "zh"
                        LanguageState.ENGLISH -> "en"
                        else -> PlatformAdapter.systemLanguage
                    }
                    LogUtils.printLog("languageCode: $languageCode")
                }

                CompositionLocalProvider(
                    LocalContextMenuRepresentation provides contextMenuRepresentation,
                    LocalLocalization provides languageCode
                ) {
                    BoxWithConstraints {
                        val windowWidth = maxWidth

                        LaunchedEffect(windowWidth) {
                            if (windowWidth < 601.dp) {
                                isMenuExpanded.value = false
                            }
                        }

                        Column(
                            modifier = Modifier.clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            WindowDraggableArea {
                                CustomTitleBar(
                                    windowState = windowState,
                                    onClose = {
                                        dialogState.value = true
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    isMenuExpanded = isMenuExpanded.value,
                                ) {
                                    isMenuExpanded.value = it
                                }
                            }

                            SplashScreen {
                                ContentView(isMenuExpanded.value)
                            }

                            if (dialogState.value) {
                                CommonDialog(
                                    title = "确认退出应用程序？",
                                    onConfirm = {
                                        exitApplication()
                                    },
                                    onCancel = { dialogState.value = false },
                                    onDismiss = { dialogState.value = false }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}