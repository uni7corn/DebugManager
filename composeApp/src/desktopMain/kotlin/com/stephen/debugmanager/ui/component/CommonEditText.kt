package com.stephen.debugmanager.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.stephen.debugmanager.ui.theme.fontPrimaryColor
import com.stephen.debugmanager.ui.theme.fontSecondaryColor
import com.stephen.debugmanager.ui.theme.infoText
import com.stephen.debugmanager.ui.theme.locationBackColor

@Composable
fun WrappedEditText(
    value: String,
    onValueChange: (String) -> Unit,
    tipText: String,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        textStyle = infoText,
        colors = TextFieldDefaults.textFieldColors(
            textColor = fontPrimaryColor,
            cursorColor = fontPrimaryColor,
            focusedIndicatorColor = fontPrimaryColor,
            unfocusedIndicatorColor = fontSecondaryColor
        ),
        label = { Text(tipText, color = fontSecondaryColor) },
        onValueChange = { onValueChange(it) },
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(locationBackColor)
            .border(2.dp, fontSecondaryColor, RoundedCornerShape(10.dp))
    )
}