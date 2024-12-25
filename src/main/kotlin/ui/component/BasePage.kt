package ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.theme.pageTitleText

@Composable
fun BasePage(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(1f).padding(horizontal = 20.dp, vertical = 10.dp)) {
        CenterText(
            title,
            modifier = Modifier.padding(bottom = 20.dp),
            style = pageTitleText,
        )
        content()
    }
}