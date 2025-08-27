package com.stephen.debugmanager.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

/**
 * 这是一个自定义的 Composable，用于为 MaterialTheme 的颜色提供动画过渡。
 */
@Composable
fun AnimatedTheme(
    targetColorScheme: ColorScheme,
    content: @Composable () -> Unit
) {
    // 对 ColorScheme 中的每个颜色进行动画
    val animatedPrimary by animateColorAsState(
        targetValue = targetColorScheme.primary,
        animationSpec = tween(durationMillis = 800)
    )
    val animatedOnPrimary by animateColorAsState(
        targetValue = targetColorScheme.onPrimary,
        animationSpec = tween(durationMillis = 800)
    )
    val animatedBackground by animateColorAsState(
        targetValue = targetColorScheme.background,
        animationSpec = tween(durationMillis = 800)
    )
    val animatedSurface by animateColorAsState(
        targetValue = targetColorScheme.surface,
        animationSpec = tween(durationMillis = 800)
    )
    val animatedOnBackground by animateColorAsState(
        targetValue = targetColorScheme.onBackground,
        animationSpec = tween(durationMillis = 800)
    )
    val animatedOnSurface by animateColorAsState(
        targetValue = targetColorScheme.onSurface,
        animationSpec = tween(durationMillis = 800)
    )
   val animatedSecondary by animateColorAsState(
        targetValue = targetColorScheme.secondary,
        animationSpec = tween(durationMillis = 800)
    )
    val animatedOnSecondary by animateColorAsState(
        targetValue = targetColorScheme.onSecondary,
        animationSpec = tween(durationMillis = 800)
    )
    // ... 对 ColorScheme 中你关心的所有颜色都这样做

    // 创建一个新的 ColorScheme，使用动画后的颜色
    val animatedColorScheme = targetColorScheme.copy(
        primary = animatedPrimary,
        onPrimary = animatedOnPrimary,
        background = animatedBackground,
        surface = animatedSurface,
        onBackground = animatedOnBackground,
        onSurface = animatedOnSurface,
        secondary = animatedSecondary,
        onSecondary = animatedOnSecondary,
    )

    // 应用动画后的颜色和内容
    MaterialTheme(
        colorScheme = animatedColorScheme,
        content = content
    )
}