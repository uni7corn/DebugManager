package com.stephen.debugmanager.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.stephen.debugmanager.ui.theme.infoText

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
            textColor = MaterialTheme.colors.onPrimary,
            cursorColor = MaterialTheme.colors.onPrimary,
            focusedIndicatorColor = MaterialTheme.colors.onPrimary,
            unfocusedIndicatorColor = MaterialTheme.colors.onSecondary
        ),
        label = { Text(tipText, color = MaterialTheme.colors.onSecondary) },
        onValueChange = { onValueChange(it) },
        modifier = modifier
            .widthIn(max = 200.dp, min = 100.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colors.secondary)
            .border(2.dp, MaterialTheme.colors.onSecondary, RoundedCornerShape(10.dp)),
    )
}