package com.stephen.debugmanager.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.stephen.composeapp.generated.resources.*
import com.stephen.debugmanager.MainStateHolder
import com.stephen.debugmanager.data.Constants
import com.stephen.debugmanager.ui.component.*
import com.stephen.debugmanager.ui.theme.groupTitleText
import org.jetbrains.compose.resources.stringResource
import org.koin.core.context.GlobalContext

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AboutPage() {
    BasePage({

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
            stringResource(Res.string.about_page_theme_setting),
            style = groupTitleText,
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
            stringResource(Res.string.about_page_language_setting),
            style = groupTitleText,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        DropdownSelector(
            Constants.languageMap.map { (key, value) ->
                key to stringResource(value)
            }.toMap(),
            languageState.value,
            modifier = Modifier.padding(bottom = 10.dp).width(120.dp)
                .padding(vertical = 5.dp)
                .clip(RoundedCornerShape(10))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            mainStateHolder.setLanguageState(it)
        }
        CenterText(
            stringResource(Res.string.about_page_cache_folder_path),
            style = groupTitleText,
            modifier = Modifier.padding(vertical = 10.dp)
        )
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
                stringResource(Res.string.about_page_open_cache_folder),
                btnColor = MaterialTheme.colorScheme.tertiary,
                onClick = { mainStateHolder.openFolder(mainStateHolder.getUserTempFilePath()) })
        }
        CenterText(
            stringResource(Res.string.about_page_pulled_folder_path),
            style = groupTitleText,
            modifier = Modifier.padding(vertical = 10.dp)
        )
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
                stringResource(Res.string.about_page_open_pulled_folder),
                btnColor = MaterialTheme.colorScheme.tertiary,
                onClick = { mainStateHolder.openFolder(mainStateHolder.getDesktopTempFolder()) })
        }
    })
}