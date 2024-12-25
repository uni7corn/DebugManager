package ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.theme.itemKeyText
import ui.theme.itemValueText

@Composable
fun NameValueText(name: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = itemKeyText,
            modifier = Modifier.weight(0.5f)
        )
        Text(
            text = value,
            style = itemValueText,
            modifier = Modifier.weight(0.5f)
        )
    }
}