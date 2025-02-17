package com.stephen.debugmanager

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.stephen.composeapp.generated.resources.Res
import com.stephen.composeapp.generated.resources.ic_compose
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
        icon = painterResource(Res.drawable.ic_compose),
    ) {
        MaterialTheme(
            colors = when (themeState.value) {
                ThemeState.DARK -> DarkColorScheme
                ThemeState.LIGHT -> LightColorScheme
                else -> if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
            }
        ) {
            SplashScreen {
                Column(modifier = Modifier.background(MaterialTheme.colors.background)) {
                    WindowDraggableArea {
                        CustomTitleBar(
                            title = "DebugManager by Stephen",
                            windowState = windowState,
                            onClose = {
                                dialogState.value = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    ContentView {
                        mainStateHolder.uninstallToolsApp()
                        exitApplication()
                    }

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