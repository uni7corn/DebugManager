package com.stephen.debugmanager.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import com.stephen.debugmanager.MainStateHolder
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.stephen.debugmanager.data.ThemeState
import org.koin.core.context.GlobalContext
import com.stephen.debugmanager.ui.component.BasePage
import com.stephen.debugmanager.ui.component.CenterText
import com.stephen.debugmanager.ui.component.ClickableLink
import com.stephen.debugmanager.ui.component.CommonButton
import com.stephen.debugmanager.ui.theme.buttonBackGroundColor
import com.stephen.debugmanager.ui.theme.groupBackGroundColor
import com.stephen.debugmanager.ui.theme.groupTitleText

@Composable
fun AboutPage() {
    BasePage("关于DebugManager") {

        val mainStateHolder by remember { mutableStateOf(GlobalContext.get().get<MainStateHolder>()) }

        val themeState = mainStateHolder.themeStateStateFlow.collectAsState()

        LaunchedEffect(Unit) {
            mainStateHolder.getThemeState()
        }

        CenterText(
            "Version: ${mainStateHolder.getDebugManagetVersion()}",
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Row(
            modifier = Modifier.padding(bottom = 20.dp),
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
            modifier = Modifier.padding(vertical = 10.dp)
                .background(MaterialTheme.colors.primary)
        )
        ThemeSwitcher(mainStateHolder, themeState.value) {
            mainStateHolder.setThemeState(it)
        }

        CenterText("缓存文件", style = groupTitleText, modifier = Modifier.padding(vertical = 10.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 10.dp)) {
            CenterText(
                mainStateHolder.getUserTempFilePath(),
                modifier = Modifier.padding(end = 20.dp),
                alignment = Alignment.CenterStart
            )
            CommonButton(
                "打开缓存目录",
                onClick = { mainStateHolder.openFolder(mainStateHolder.getUserTempFilePath()) })
        }
        CenterText("PULL的Android文件存储目录", style = groupTitleText, modifier = Modifier.padding(vertical = 10.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 10.dp)) {
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

@Composable
fun ThemeSwitcher(mainStateHolder: MainStateHolder, currentTheme: Int, onThemeChange: (Int) -> Unit) {
    val themeMap = mapOf<String, Int>(
        "深色" to ThemeState.DARK,
        "浅色" to ThemeState.LIGHT,
        "跟随系统" to ThemeState.SYSTEM
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 10.dp)
            .width(IntrinsicSize.Max)
            .clip(RoundedCornerShape(50))
            .background(groupBackGroundColor)
            .padding(10.dp)
    ) {
        themeMap.forEach {
            val themeName = it.key
            val themeValue = it.value
            CenterText(
                text = themeName,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(if (currentTheme == themeValue) buttonBackGroundColor else Color.Transparent)
                    .clickable {
                        onThemeChange(themeValue)
                    }
                    .padding(vertical = 5.dp, horizontal = 10.dp)
            )
        }
    }
}
