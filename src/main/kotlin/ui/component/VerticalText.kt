package ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle

@Composable
fun VerticalText(text: String, style: TextStyle) {
    Column {
        text.toCharArray().forEach { char ->
            CenterText(
                text = char.toString(),
                style = style
            )
        }
    }
}