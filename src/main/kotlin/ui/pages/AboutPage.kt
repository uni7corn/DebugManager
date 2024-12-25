package ui.pages

import MainStateHolder
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.core.context.GlobalContext
import ui.component.BasePage
import ui.component.CenterText
import ui.component.CommonButton
import ui.theme.groupTitleText

@Composable
fun AboutPage() {
    BasePage("关于DebugManager") {

        val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

        CenterText(
            "Version: ${mainStateHolder.getDebugManagetVersion()}",
            style = groupTitleText,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        CenterText("缓存文件", style = groupTitleText)
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 20.dp)) {
            CenterText(
                mainStateHolder.getUserTempFilePath(),
                modifier = Modifier.padding(end = 20.dp),
                alignment = Alignment.CenterStart
            )
            CommonButton(
                "打开缓存目录",
                onClick = { mainStateHolder.openFolder(mainStateHolder.getUserTempFilePath()) })
        }
        CenterText("PULL的Android文件存储目录", style = groupTitleText)
        Row(verticalAlignment = Alignment.CenterVertically) {
            CenterText(
                mainStateHolder.getDesktopTempFolder(),
                modifier = Modifier.padding(end = 20.dp),
                alignment = Alignment.CenterStart
            )
            CommonButton(
                "打开Android缓存目录",
                onClick = { mainStateHolder.openFolder(mainStateHolder.getDesktopTempFolder()) })
        }
    }
}
