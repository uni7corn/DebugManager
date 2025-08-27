package com.stephen.debugmanager.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.mikepenz.markdown.model.MarkdownColors

// 网络链接颜色
val networkTextColor = Color(0xff62bffc)

val markDownDark = object : MarkdownColors {
    override val text: Color
        get() = DarkColorScheme.onPrimary
    override val codeBackground: Color
        get() = DarkColorScheme.surface
    override val codeText: Color
        get() = DarkColorScheme.onPrimary
    override val dividerColor: Color
        get() = DarkColorScheme.secondary
    override val inlineCodeBackground: Color
        get() = DarkColorScheme.surface
    override val inlineCodeText: Color
        get() = DarkColorScheme.onPrimary
    override val linkText: Color
        get() = networkTextColor
    override val tableBackground: Color
        get() = DarkColorScheme.surface
    override val tableText: Color
        get() = DarkColorScheme.onPrimary
}

val markDownLight = object : MarkdownColors {
    override val text: Color
        get() = LightColorScheme.onPrimary
    override val codeBackground: Color
        get() = LightColorScheme.surface
    override val codeText: Color
        get() = LightColorScheme.onPrimary
    override val dividerColor: Color
        get() = LightColorScheme.secondary
    override val inlineCodeBackground: Color
        get() = LightColorScheme.surface
    override val inlineCodeText: Color
        get() = LightColorScheme.onPrimary
    override val linkText: Color
        get() = networkTextColor
    override val tableBackground: Color
        get() = LightColorScheme.surface
    override val tableText: Color
        get() = LightColorScheme.onPrimary
}

// 给开屏页做动画过渡
val DarkBackGround = Color(0xFF1a1a1a)
val LightBackGround = Color(0xffe2e9f8)

val DarkColorScheme = darkColorScheme(
    // 使用最多的按钮色
    primary = Color(0xff404040),
    //大背景
    background = Color(0xFF1a1a1a),
    // 功能组背景
    surface = Color(0xff282828),
    // 文字颜色
    onPrimary = Color(0xffebebeb),
    // 路径框，输入框
    secondary = Color(0xFF1a1a1a),
    // 特殊按钮颜色二
    tertiary = Color(0xff3d77c2),
    // 置灰文字，提示文字颜色
    onSecondary = Color(0xff999999),
    // 警告按钮或错误提示
    error = Color(0xffe53c3c),
    // 背景之上的其余元素的颜色，目前只有分割线
    onBackground = Color(0xff323232),
    // 功能组选中的颜色
    onSurface = Color(0xff404040),
)

val LightColorScheme = lightColorScheme(
    primary = Color(0xfffefeff),
    background = Color(0xffe2e9f8),
    surface = Color(0xffeff2fd),
    onPrimary = Color(0xff2b2f35),
    secondary = Color(0xffe2e9f8),
    tertiary =Color(0xff62bffc) ,
    onSecondary = Color(0xff9197a1),
    error = Color(0xffe8563d),
    onBackground = Color(0xffe2e5f0),
    onSurface = Color(0xffd6deec),
)