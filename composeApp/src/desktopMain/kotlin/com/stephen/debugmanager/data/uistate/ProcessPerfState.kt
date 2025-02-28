package com.stephen.debugmanager.data.uistate

data class ProcessPerfState(
    val userId: String = "",
    val pid: String = "",
    val rss: String = "",
    val cpu: String = "",
    val processName: String = "",
)