package com.stephen.debugmanager.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.stephen.composeapp.generated.resources.*
import com.stephen.debugmanager.MainStateHolder
import com.stephen.debugmanager.base.getLanguageCode
import com.stephen.debugmanager.base.getLanguageLocale
import com.stephen.debugmanager.data.ThemeState
import com.stephen.debugmanager.ui.component.AnimatedTheme
import com.stephen.debugmanager.ui.component.CenterText
import com.stephen.debugmanager.ui.component.CommonButton
import com.stephen.debugmanager.ui.component.SimpleDivider
import com.stephen.debugmanager.ui.theme.DarkColorScheme
import com.stephen.debugmanager.ui.theme.LightColorScheme
import com.stephen.debugmanager.utils.LogUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.context.GlobalContext
import java.util.*

@Composable
fun ApplicationScope.SingleProcessTipWindow() {

    val mainStateHolder = GlobalContext.get().get<MainStateHolder>()

    val themeState = mainStateHolder.themeStateStateFlow.collectAsState()

    val languageState = mainStateHolder.languageStateStateFlow.collectAsState()

    val windowState = rememberWindowState(
        width = 300.dp,
        height = 180.dp,
        position = WindowPosition.Aligned(Alignment.Center)
    )

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        mainStateHolder.callSystemAlert()
    }

    Window(
        onCloseRequest = {
            exitApplication()
        },
        state = windowState,
        transparent = true,
        title = "DebugManager",
        undecorated = true,
        resizable = false,
        icon = painterResource(Res.drawable.ic_error_tip),
    ) {
        AnimatedTheme(
            targetColorScheme = when (themeState.value) {
                ThemeState.DARK -> DarkColorScheme
                ThemeState.LIGHT -> LightColorScheme
                else -> if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
            }
        ) {
            val LocalLocalization = compositionLocalOf { "en" }
            var languageCode by remember { mutableStateOf("en") }

            LaunchedEffect(languageState.value) {
                LogUtils.printLog("languageState.value: ${languageState.value}")
                val locale = getLanguageLocale(languageState.value)
                Locale.setDefault(locale)
                languageCode = getLanguageCode(languageState.value)
                LogUtils.printLog("languageCode: $languageCode")
            }

            CompositionLocalProvider(LocalLocalization provides languageCode) {
                Surface(
                    modifier = Modifier.fillMaxSize(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, MaterialTheme.colorScheme.onSecondary, RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    WindowDraggableArea {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // 标题栏
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .background(MaterialTheme.colorScheme.surface),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(1f),
                                ) {
                                    Row(
                                        modifier = Modifier.align(Alignment.Center),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        CenterText(
                                            text = stringResource(Res.string.app_name),
                                            modifier = Modifier.padding(start = 4.dp)
                                        )
                                    }

                                    Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                                        Image(
                                            contentDescription = "close",
                                            painter = painterResource(Res.drawable.ic_close),
                                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.error),
                                            modifier = Modifier.clickable { exitApplication() }.size(32.dp)
                                                .padding(5.dp)
                                        )
                                    }
                                    SimpleDivider(
                                        modifier = Modifier.fillMaxWidth(1f).height(1.dp).align(Alignment.BottomCenter)
                                    )
                                }
                            }

                            // 内容区域
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth(1f).padding(5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CenterText(text = stringResource(Res.string.single_process_tip))
                            }
                            Box(
                                modifier = Modifier.fillMaxWidth(1f),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                CommonButton(
                                    text = stringResource(Res.string.dialog_ok), onClick = {
                                        scope.launch {
                                            delay(200L)
                                            exitApplication()
                                        }
                                    },
                                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 10.dp)
                                        .width(100.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}