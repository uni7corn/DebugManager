package com.stephen.debugmanager.data.uistate

data class ProcessPerfState(
    val userId: String = "",
    val pid: String = "",
    val vsz: String = "",
    val rss: String = "",
    val cpu: String = "",
    val processName: String = "",
) {
    fun toUiState() = ProcessPerfState(
        userId = userId,
        pid = pid,
        vsz = vsz,
        rss = rss,
        cpu = cpu,
        processName = processName,
    )
}