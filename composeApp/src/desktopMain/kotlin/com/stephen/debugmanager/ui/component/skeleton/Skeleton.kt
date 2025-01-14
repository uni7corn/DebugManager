package com.stephen.debugmanager.ui.component.skeleton

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.stephen.debugmanager.ui.theme.groupBackGroundColor

object WeSkeleton {

    @Composable
    fun Rectangle(isActive: Boolean = true, content: @Composable () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(groupBackGroundColor)
                .shimmerLoading(isActive)
        ) {
            // 停止加载时显示内容，透明度动画稍慢
            AnimatedVisibility(
                !isActive,
                enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessLow))
            ) {
                content()
            }
        }
    }
}
