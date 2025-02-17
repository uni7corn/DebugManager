package com.stephen.debugmanager.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CommonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color:Color = MaterialTheme.colors.primary
) {
    CenterText(
        modifier = modifier
            .width(IntrinsicSize.Max)
            .clickable {
            onClick()
        }.clip(RoundedCornerShape(10))
            .background(color)
            .shadow(1.dp, spotColor = Color.White)
            .padding(5.dp),
        text = text
    )
}