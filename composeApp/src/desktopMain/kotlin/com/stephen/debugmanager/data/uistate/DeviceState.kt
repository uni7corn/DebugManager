package com.stephen.debugmanager.data.uistate

data class DeviceState(
    val name: String? = null,
    val manufacturer: String? = null,
    val sdkVersion: String? = null,
    val systemVersion: String? = null,
    val buildType: String? = null,
    val innerName: String? = null,
    val resolution: String? = null,
    val density: String? = null,
    val cpuArch: String? = null,
    val serial:String? = null,
    val isConnected: Boolean = false
) {
    fun toUiState() =
        DeviceState(
            name = name,
            systemVersion = systemVersion,
            manufacturer = manufacturer,
            sdkVersion = sdkVersion,
            buildType = buildType,
            innerName = innerName,
            resolution = resolution,
            cpuArch = cpuArch,
            density = density,
            serial = serial,
            isConnected = isConnected
        )
}