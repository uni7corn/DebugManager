package com.stephen.debugmanager.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.stephen.debugmanager.MainStateHolder
import com.stephen.debugmanager.data.Constants
import com.stephen.debugmanager.data.ThemeState
import com.stephen.debugmanager.ui.component.BasePage
import com.stephen.debugmanager.ui.component.CenterText
import com.stephen.debugmanager.ui.component.ClickableLink
import com.stephen.debugmanager.ui.component.CommonButton
import com.stephen.debugmanager.ui.component.RadioGroupSwitcher
import com.stephen.debugmanager.ui.theme.groupTitleText
import org.koin.core.context.GlobalContext

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AboutPage() {
    BasePage("关于DebugManager") {

        val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

        val themeState = mainStateHolder.themeStateStateFlow.collectAsState()

        val languageState = mainStateHolder.languageStateStateFlow.collectAsState()

        CenterText(
            "Version: ${mainStateHolder.getDebugManagetVersion()}",
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Row(
            modifier = Modifier.padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CenterText(
                "Github Link:",
                modifier = Modifier.padding(end = 10.dp)
            )
            ClickableLink(
                "https://github.com/stepheneasyshot/DebugManager",
                "https://github.com/stepheneasyshot/DebugManager"
            )
        }

        CenterText(
            "主题设置", style = groupTitleText,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        RadioGroupSwitcher(
            Constants.themeMap,
            themeState.value,
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            mainStateHolder.setThemeState(it)
        }

        CenterText(
            "语言设置", style = groupTitleText,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        RadioGroupSwitcher(
            Constants.languageMap,
            languageState.value,
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            mainStateHolder.setLanguageState(it)
        }

        CenterText("缓存文件", style = groupTitleText, modifier = Modifier.padding(vertical = 10.dp))

        FlowRow(
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            CenterText(
                mainStateHolder.getUserTempFilePath(),
                modifier = Modifier.padding(end = 20.dp).fillMaxRowHeight(1f),
                alignment = Alignment.CenterStart
            )
            CommonButton(
                "打开缓存目录",
                btnColor = MaterialTheme.colorScheme.tertiary,
                onClick = { mainStateHolder.openFolder(mainStateHolder.getUserTempFilePath()) })
        }
        CenterText("PULL的Android文件存储目录", style = groupTitleText, modifier = Modifier.padding(vertical = 10.dp))
        FlowRow(
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            CenterText(
                mainStateHolder.getDesktopTempFolder(),
                modifier = Modifier.padding(end = 20.dp).fillMaxRowHeight(1f),
                alignment = Alignment.CenterStart
            )
            CommonButton(
                "打开Android缓存目录",
                btnColor = MaterialTheme.colorScheme.tertiary,
                onClick = { mainStateHolder.openFolder(mainStateHolder.getDesktopTempFolder()) })
        }
    }
}