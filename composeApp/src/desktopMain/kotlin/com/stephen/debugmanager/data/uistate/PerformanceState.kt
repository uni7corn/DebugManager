package com.stephen.debugmanager.data.uistate

data class PerformanceState(
    val cpuTotal: String = "",
    val cpuUser: String = "",
    val cpuNice: String = "",
    val cpuSys: String = "",
    val cpuIdle: String = "",
    val cpuIOWait: String = "",
    val cpuIRQ: String = "",
    val cpuSoftIRQ: String = "",
    val cpuHost: String = "",
    val memFree: String = "",
    val memTotal: String = "",
    val memUsed: String = "",
) {
    fun toUiState() = PerformanceState(
        cpuTotal = cpuTotal,
        cpuUser = cpuUser,
        cpuNice = cpuNice,
        cpuIdle = cpuIdle,
        cpuSys = cpuSys,
        cpuIOWait = cpuIOWait,
        cpuIRQ = cpuIRQ,
        cpuSoftIRQ = cpuSoftIRQ,
        cpuHost = cpuHost,
        memFree = memFree,
        memTotal = memTotal,
        memUsed = memUsed,
    )
}