package com.stephen.debugmanager.base

import com.stephen.debugmanager.data.AndroidDevice
import kotlinx.coroutines.*
import com.stephen.debugmanager.utils.LogUtils

class AdbClient(private val platformAdapter: PlatformAdapter) {

    private val androidDeviceMap = mutableMapOf<String, AndroidDevice>()

    fun init() {
        // start-server不能带串口参数，直接adb start-server
        // 使用带result的方法来执行，必须等adb成功启动之后，再进行下一步的client初始化
        MainScope().launch {
            platformAdapter.executeCommandWithResult("${platformAdapter.localAdbPath} start-server")
        }
    }

    var serial: String = ""

    fun setChoosedDevice(serialNumber: String) {
        serial = serialNumber
    }

    suspend fun isAdbServerStarted(): Boolean {
        return platformAdapter.executeCommandWithResult("${platformAdapter.localAdbPath} devices")
            .contains("List of devices attached")
    }

    suspend fun whenDevicesConnected(onDeviceConnected: suspend () -> Unit) = withContext(Dispatchers.IO) {
        while (androidDeviceMap.isEmpty()) {
            LogUtils.printLog("No Devices Connected!")
            delay(500L)
        }
        onDeviceConnected()
    }

    /**
     * 解析adb devices输出获取设备列表
     */
    private fun parseAdbDevicesOutput(output: String): List<String> {
        return output.lines()
            .asSequence()
            .filter { it.isNotBlank() }
            .drop(1) // 跳过默认的 "List of devices attached" 标题行
            .filter { line ->
                // 定义所有有效的设备状态关键字集合
                val validStates = setOf("device", "emulator", "bootloader", "recovery", "fastboot")
                validStates.any { line.contains(it) }
            }
            .map { line ->
                // 分割设备序列号和状态（如"device"、"offline"等）
                val parts = line.split("\\s+".toRegex())
                if (parts.isNotEmpty()) parts[0] else ""
            }
            .toList()
    }

    /**
     * 获取当前连接的设备列表
     */
    suspend fun getAdbDevicesList() = parseAdbDevicesOutput(
        platformAdapter.executeCommandWithResult(
            "${platformAdapter.localAdbPath} devices",
            false
        )
    )

    /**
     * 获取ROOT权限
     */
    fun runRootScript() {
        CoroutineScope(Dispatchers.IO).launch {
            platformAdapter.executeTerminalCommand("${platformAdapter.localAdbPath} $serial root")
        }
    }

    /**
     *  重载REMOUNT
     */
    fun runRemountScript() {
        CoroutineScope(Dispatchers.IO).launch {
            platformAdapter.executeTerminalCommand("${platformAdapter.localAdbPath} $serial remount")
        }
    }

    /**
     * 获取设备名和串口id的映射关系
     * Client未初始化完成，循环等待
     */
    suspend fun getDeviceMap() = withContext(Dispatchers.IO) {
        // 等待adb服务器启动
        while (!isAdbServerStarted()) {
            delay(1000L)
        }

        val devices = getAdbDevicesList() // 获取解析后的设备列表

        LogUtils.printLog("Connected Device Number:${devices.size}")

        // 如果无设备或者设备减少，则清空map
        if (devices.isEmpty() || devices.size < androidDeviceMap.size) {
            androidDeviceMap.clear()
        }

        devices.forEachIndexed { index, serialNumber ->
            // 创建设备实例并添加到映射，获取设备名称，初次添加列表长度为0，不可用内部方法
            val deviceName =
                platformAdapter.executeCommandWithResult(
                    "${platformAdapter.localAdbPath} -s $serialNumber shell getprop ro.product.model"
                )
            androidDeviceMap[serialNumber] = AndroidDevice(deviceName, serialNumber)
        }

        // 默认选择第一个设备
        if (devices.isNotEmpty()) {
            setChoosedDevice(devices[0])
        }

        androidDeviceMap
    }

    /**
     * 获取$localAdbPath Shell执行结果
     */
    suspend fun getAndroidShellExecuteResult(serial: String, command: String, isRemoveReturn: Boolean = true) =
        if (androidDeviceMap.isNotEmpty())
            platformAdapter.executeCommandWithResult(
                "${platformAdapter.localAdbPath} -s $serial shell $command",
                isRemoveReturn
            )
        else ""

}