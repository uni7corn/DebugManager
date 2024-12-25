package model.uistate

data class DeviceMapState(
    val deviceMap: Map<String, String> = mapOf(),
    val currentChoosedDevice: Int = 0,
) {
    fun toUiState() = DeviceMapState(deviceMap = deviceMap, currentChoosedDevice = currentChoosedDevice)
}