package com.stephen.debugmanager.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stephen.debugmanager.ui.theme.pageTitleText

@Composable
fun BasePage(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(1f).padding(10.dp)) {
        CenterText(
            title,
            modifier = Modifier.padding(bottom = 20.dp).align(Alignment.CenterHorizontally),
            style = pageTitleText,
        )
        content()
    }
}