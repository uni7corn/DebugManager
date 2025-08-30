package com.stephen.debugmanager.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.stephen.debugmanager.ui.theme.defaultText

@Composable
fun CommonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    btnColor: Color = MaterialTheme.colorScheme.primary,
    textModifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.bounceClick().clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        ).clip(RoundedCornerShape(12))
            .background(
                brush = Brush.verticalGradient( // 垂直渐变模拟反光
                    colors = listOf(
                        btnColor.copy(alpha = 0.8f), // 顶部稍亮
                        btnColor,
                        btnColor.copy(alpha = 0.9f) // 底部稍亮
                    ),
                    startY = 0f,
                    endY = 150f
                )
            ).padding(vertical = 6.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        // 禁止换行
        CenterText(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            style = defaultText,
            modifier = textModifier,
        )
    }
}

enum class ButtonState { Pressed, Idle }

fun Modifier.bounceClick() = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val scale by animateFloatAsState(if (buttonState == ButtonState.Pressed) 0.90f else 1f)

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }.pointerInput(buttonState) {
        awaitPointerEventScope {
            buttonState = if (buttonState == ButtonState.Pressed) {
                waitForUpOrCancellation()
                ButtonState.Idle
            } else {
                awaitFirstDown(false)
                ButtonState.Pressed
            }
        }
    }
}