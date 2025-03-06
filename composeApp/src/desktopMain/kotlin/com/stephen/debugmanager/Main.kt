package com.stephen.debugmanager

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.stephen.composeapp.generated.resources.Res
import com.stephen.composeapp.generated.resources.app_logo
import com.stephen.debugmanager.data.ThemeState
import com.stephen.debugmanager.di.koinModules
import com.stephen.debugmanager.ui.ContentView
import com.stephen.debugmanager.ui.component.CommonDialog
import com.stephen.debugmanager.ui.component.CustomTitleBar
import com.stephen.debugmanager.ui.pages.SplashScreen
import com.stephen.debugmanager.ui.theme.DarkColorScheme
import com.stephen.debugmanager.ui.theme.LightColorScheme
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import java.awt.Dimension

fun main() = application {

    startKoin {
        modules(koinModules)
    }

    val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

    val windowState = rememberWindowState(width = 1000.dp, height = 650.dp)

    val dialogState = remember { mutableStateOf(false) }

    val themeState = mainStateHolder.themeStateStateFlow.collectAsState()

    LaunchedEffect(Unit) {
        // 获取存储的主题设置
        mainStateHolder.getThemeState()
    }

    Window(
        onCloseRequest = {
            if (windowState.isMinimized)
                windowState.isMinimized = false
            dialogState.value = true
        },
        title = "DebugManager",
        undecorated = true,
        state = windowState,
        icon = painterResource(Res.drawable.app_logo),
    ) {
        // set the minimum size
        window.minimumSize = Dimension(600, 650)

        MaterialTheme(
            colorScheme = when (themeState.value) {
                ThemeState.DARK -> DarkColorScheme
                ThemeState.LIGHT -> LightColorScheme
                else -> if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
            }
        ) {
            SplashScreen {
                BoxWithConstraints {
                    val windowWidth = maxWidth
                    Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                        WindowDraggableArea {
                            CustomTitleBar(
                                title = "DebugManager",
                                windowState = windowState,
                                onClose = {
                                    dialogState.value = true
                                },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }

                        ContentView(windowWidth)

                        if (dialogState.value) {
                            CommonDialog(
                                title = "确认退出应用程序？",
                                onConfirm = {
                                    mainStateHolder.uninstallToolsApp()
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