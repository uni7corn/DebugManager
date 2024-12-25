import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import di.koinModules
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import ui.ContentView
import ui.component.CenterText
import ui.component.CommonDialog
import ui.theme.backGroundColor
import ui.theme.groupBackGroundColor

/**
 * 应用入口
 */
fun main() = application {

    startKoin {
        modules(koinModules)
    }

    val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

    val windowState = rememberWindowState(width = 1100.dp, height = 650.dp)

    val dialogState = remember { mutableStateOf(false) }

    Window(
        onCloseRequest = {
            if (windowState.isMinimized)
                windowState.isMinimized = false
            dialogState.value = true
        },
        title = "DebugManager",
        undecorated = true,
        state = windowState,
        icon = painterResource("image/icon.png"),
    ) {
        Column(modifier = Modifier.background(backGroundColor)) {
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
                CommonDialog(title = "确认退出应用程序？",
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

@Composable
fun CustomTitleBar(
    title: String,
    windowState: WindowState,
    onClose: () -> Unit,
    onMinimize: () -> Unit = {
        windowState.isMinimized = true
    },
    onMaximize: () -> Unit = {
        windowState.placement = if (windowState.placement == WindowPlacement.Maximized)
            WindowPlacement.Floating else WindowPlacement.Maximized
    },
    modifier: Modifier = Modifier
) {
    val isMaximized = windowState.placement == WindowPlacement.Maximized
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp)
            .background(groupBackGroundColor),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    contentDescription = "logo",
                    painter = painterResource("image/icon.png"),
                    modifier = Modifier.padding(5.dp).clip(RoundedCornerShape(10))
                )
                CenterText(text = title, modifier = Modifier.padding(start = 4.dp))
            }

            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                Image(
                    contentDescription = "minimize",
                    painter = painterResource("image/ic_minimize.png"),
                    modifier = Modifier.clickable { onMinimize() }.size(40.dp).padding(5.dp)
                )
                Image(
                    contentDescription = "maximize",
                    painter = if (isMaximized) painterResource("image/ic_floating.png")
                    else painterResource("image/ic_maximize.png"),
                    modifier = Modifier.clickable { onMaximize() }.size(40.dp).padding(5.dp)
                )
                Image(
                    contentDescription = "close",
                    painter = painterResource("image/ic_close.png"),
                    modifier = Modifier.clickable { onClose() }.size(40.dp).padding(2.dp)
                )
            }
        }
    }
}