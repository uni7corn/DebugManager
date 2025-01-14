package com.stephen.debugmanager.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.stephen.debugmanager.ui.theme.networkTextColor
import java.awt.Desktop
import java.net.URI

@Composable
fun ClickableLink(linkText: String, url: String) {
    val toastState = rememberToastState()
    val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
    CenterText(
        text = linkText,
        modifier = Modifier
            .clickable {
                // 编码URL以处理特殊字符
                val encodedUrl = URI(url)
                desktop?.let {
                    if (it.isSupported(Desktop.Action.BROWSE))
                        it.browse(encodedUrl)
                    else
                        toastState.show("不支持打开链接")
                } ?: run {
                    toastState.show("不支持打开链接")
                }
            },
        style = TextStyle(
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline
        ),
        color = networkTextColor
    )
}
