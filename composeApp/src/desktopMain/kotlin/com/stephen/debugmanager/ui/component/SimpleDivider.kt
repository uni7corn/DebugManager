package com.stephen.debugmanager.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SimpleDivider(modifier: Modifier = Modifier) {
    Spacer(modifier.background(MaterialTheme.colorScheme.onBackground))
}