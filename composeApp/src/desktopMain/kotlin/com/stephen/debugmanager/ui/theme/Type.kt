package com.stephen.debugmanager.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.model.MarkdownTypography

val defaultText = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Center,
)

val infoText = TextStyle(
    fontSize = 14.sp,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Left,
)

val pageTitleText = TextStyle(
    fontSize = 20.sp,
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Center,
)

val groupTitleText = TextStyle(
    fontSize = 18.sp,
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Center,
)

val itemKeyText = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Left,
)

val itemValueText = TextStyle(
    fontSize = 14.sp,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Left,
)

// 新增 h1 到 h6 的字体配置
val h1Text = TextStyle(
    fontSize = 22.sp,
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Left
)

val h2Text = TextStyle(
    fontSize = 20.sp,
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Left
)

val h3Text = TextStyle(
    fontSize = 18.sp,
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Left
)

val h4Text = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Left
)

val h5Text = TextStyle(
    fontSize = 14.sp,
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Left
)

val h6Text = TextStyle(
    fontSize = 12.sp,
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Left
)

val markdownDefaultText = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Left,
)

val markdownTypography = object : MarkdownTypography {
    override val text: TextStyle
        get() = markdownDefaultText
    override val code: TextStyle
        get() = markdownDefaultText
    override val inlineCode: TextStyle
        get() = markdownDefaultText
    override val h1: TextStyle
        get() = h1Text
    override val h2: TextStyle
        get() = h2Text
    override val h3: TextStyle
        get() = h3Text
    override val h4: TextStyle
        get() = h4Text
    override val h5: TextStyle
        get() = h5Text
    override val h6: TextStyle
        get() = h6Text
    override val quote: TextStyle
        get() = markdownDefaultText
    override val paragraph: TextStyle
        get() = markdownDefaultText
    override val ordered: TextStyle
        get() = markdownDefaultText
    override val bullet: TextStyle
        get() = markdownDefaultText
    override val list: TextStyle
        get() = markdownDefaultText
    override val link: TextStyle
        get() = markdownDefaultText
}