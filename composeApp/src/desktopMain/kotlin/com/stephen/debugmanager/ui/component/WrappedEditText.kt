package com.stephen.debugmanager.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onPrimary,
            cursorColor = MaterialTheme.colorScheme.onPrimary,
            focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimary
        ),
        label = { Text(tipText, color = MaterialTheme.colorScheme.onSecondary) },
        onValueChange = { onValueChange(it) },
        modifier = modifier
            .widthIn(max = 200.dp, min = 100.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .border(2.dp, MaterialTheme.colorScheme.onSecondary, RoundedCornerShape(10.dp)),
    )
}