package com.stephen.debugmanager.model.uistate

import androidx.compose.ui.graphics.ImageBitmap

data class AppListState(
    val appList: List<AppItemData> = listOf(),
) {
    fun toUiState() = AppListState(appList = appList)
}

data class AppItemData(
    val packageName: String,
    val appLabel: String,
    val version: String,
    val icon: ImageBitmap,
    val lastUpdateTime: String
)