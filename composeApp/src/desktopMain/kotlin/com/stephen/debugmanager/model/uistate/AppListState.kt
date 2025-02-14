package com.stephen.debugmanager.model.uistate

import androidx.compose.ui.graphics.ImageBitmap

data class AppListState(
    val appList: List<AppItemData> = listOf(),
    val listSize:Int = appList.size
) {
    fun toUiState() = AppListState(appList = appList, listSize = listSize)
}

data class AppItemData(
    val packageName: String,
    val appLabel: String,
    val version: String,
    val icon: ImageBitmap,
    val lastUpdateTime: String
)