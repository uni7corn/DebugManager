package com.stephen.debugmanager.data.uistate


data class DeviceMapState(
    val deviceMap: Map<String, String> = mapOf(),
    val choosedSerial: String = "",
) {
    fun toUiState() = DeviceMapState(deviceMap = deviceMap, choosedSerial = choosedSerial)
}