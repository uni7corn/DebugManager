package com.stephen.debugmanager.data.uistate

data class AppListState(
    val appMap: Map<String, AppItemData> = mapOf(),
    val listSize: Int = appMap.size
) {
    fun toUiState() = AppListState(appMap = appMap, listSize = listSize)
}

data class AppItemData(
    val packageName: String,
    val appLabel: String,
    val version: String,
    val iconFilePath: String,
    val lastUpdateTime: String
)