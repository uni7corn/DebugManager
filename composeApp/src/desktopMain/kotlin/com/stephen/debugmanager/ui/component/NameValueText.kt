package com.stephen.debugmanager.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stephen.debugmanager.ui.theme.itemKeyText
import com.stephen.debugmanager.ui.theme.itemValueText

@Composable
fun NameValueText(name: String, value: String, modifier: Modifier = Modifier, nameWeight: Float = 0.5f) {
    Row(
        modifier = modifier.padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = itemKeyText,
            modifier = Modifier.weight(nameWeight),
            color = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = value,
            style = itemValueText,
            modifier = Modifier.weight(1f - nameWeight),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}