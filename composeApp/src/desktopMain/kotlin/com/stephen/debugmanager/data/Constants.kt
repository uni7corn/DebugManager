package com.stephen.debugmanager.data

import com.stephen.composeapp.generated.resources.Res
import com.stephen.composeapp.generated.resources.ic_about
import com.stephen.composeapp.generated.resources.ic_devices
import com.stephen.composeapp.generated.resources.ic_file_manage
import com.stephen.composeapp.generated.resources.ic_performance
import com.stephen.composeapp.generated.resources.ic_robot
import com.stephen.composeapp.generated.resources.ic_software
import com.stephen.composeapp.generated.resources.ic_terminal
import com.stephen.composeapp.generated.resources.ic_tools
import com.stephen.composeapp.generated.resources.language_arabic
import com.stephen.composeapp.generated.resources.language_chinese
import com.stephen.composeapp.generated.resources.language_english
import com.stephen.composeapp.generated.resources.language_french
import com.stephen.composeapp.generated.resources.language_german
import com.stephen.composeapp.generated.resources.language_hindi
import com.stephen.composeapp.generated.resources.language_japanese
import com.stephen.composeapp.generated.resources.language_korean
import com.stephen.composeapp.generated.resources.language_russian
import com.stephen.composeapp.generated.resources.language_spanish
import com.stephen.composeapp.generated.resources.side_menu_about
import com.stephen.composeapp.generated.resources.side_menu_aimodel
import com.stephen.composeapp.generated.resources.side_menu_app_manege
import com.stephen.composeapp.generated.resources.side_menu_command
import com.stephen.composeapp.generated.resources.side_menu_deviceinfo
import com.stephen.composeapp.generated.resources.side_menu_filemanage
import com.stephen.composeapp.generated.resources.side_menu_performance
import com.stephen.composeapp.generated.resources.side_menu_tools
import com.stephen.composeapp.generated.resources.system_default
import com.stephen.composeapp.generated.resources.theme_dark
import com.stephen.composeapp.generated.resources.theme_light
import com.stephen.composeapp.generated.resources.theme_system
import com.stephen.debugmanager.data.bean.MainTabItem

object Constants {
    //  主条目
    const val DEVICE_INFO = "DEVICE_INFO"
    const val APP_MANAGE = "APP_MANAGE"
    const val FILE_MANAGE = "FILE_MANAGE"
    const val COMMAND = "COMMAND"
    const val PERFORMANCE = "PERFORMANCE"
    const val TOOLS = "TOOLS"
    const val ABOUT = "ABOUT"
    const val AI_MODEL = "AI_MODEL"

    val mainItemMap = mapOf(
        DEVICE_INFO to MainTabItem(Res.string.side_menu_deviceinfo, Res.drawable.ic_devices),
        APP_MANAGE to MainTabItem(Res.string.side_menu_app_manege, Res.drawable.ic_software),
        FILE_MANAGE to MainTabItem(Res.string.side_menu_filemanage, Res.drawable.ic_file_manage),
        COMMAND to MainTabItem(Res.string.side_menu_command, Res.drawable.ic_terminal),
        PERFORMANCE to MainTabItem(Res.string.side_menu_performance, Res.drawable.ic_performance),
        TOOLS to MainTabItem(Res.string.side_menu_tools, Res.drawable.ic_tools),
        AI_MODEL to MainTabItem(Res.string.side_menu_aimodel, Res.drawable.ic_robot),
        ABOUT to MainTabItem(Res.string.side_menu_about, Res.drawable.ic_about),
    )

    val themeMap = mapOf(
        Res.string.theme_dark to ThemeState.DARK,
        Res.string.theme_light to ThemeState.LIGHT,
        Res.string.theme_system to ThemeState.SYSTEM
    )

    val languageMap = mapOf(
        LanguageState.CHINESE to Res.string.language_chinese,
        LanguageState.ENGLISH to Res.string.language_english,
        LanguageState.RUSSIAN to Res.string.language_russian,
        LanguageState.HINDI to Res.string.language_hindi,
        LanguageState.SPANISH to Res.string.language_spanish,
        LanguageState.FRENCH to Res.string.language_french,
        LanguageState.GERMAN to Res.string.language_german,
        LanguageState.KOREAN to Res.string.language_korean,
        LanguageState.JAPANESE to Res.string.language_japanese,
        LanguageState.ARABIC to Res.string.language_arabic,
        LanguageState.AUTO to Res.string.system_default
    )
}