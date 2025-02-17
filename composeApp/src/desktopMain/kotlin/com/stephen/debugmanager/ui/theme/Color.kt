package com.stephen.debugmanager.ui.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

// 网络链接颜色
val networkTextColor = Color(0xff62bffc)


val DarkColorScheme = darkColors(
    // 使用最多的按钮色
    primary = Color(0xff575e6e),
    //大背景
    background = Color(0xFF303845),
    // 功能组背景
    surface = Color(0xff3e4653),
    // 文字颜色
    onPrimary = Color(0xffc6c6cd),
    // 路径框，输入框
    secondary = Color(0xFF2e3643),
    // 置灰文字，提示文字颜色
    onSecondary = Color(0xccc6c6cd),
    // 警告按钮或错误提示
    error = Color(0xffc04645),
    // 背景之上的其余元素的颜色
    onBackground = Color(0xccc6c6cd),
    // 功能组选中的颜色
    onSurface = Color(0xff4d5972),
)

val LightColorScheme = lightColors(
    primary = Color(0xffe7c496),
    secondary = Color(0xffd0ebee),
    background = Color(0xFFfff8ed),
    surface = Color(0xfff6d7b0),
    onPrimary = Color(0xff272727),
    error = Color(0xffff5370),
    onSecondary = Color(0xff888477),
    onBackground = Color(0xffd9d4c7),
    onSurface = Color(0xfffbead5),
)