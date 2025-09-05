package com.stephen.debugmanager.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BasePage(content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(1f).padding(10.dp)) {
        content()
    }
}