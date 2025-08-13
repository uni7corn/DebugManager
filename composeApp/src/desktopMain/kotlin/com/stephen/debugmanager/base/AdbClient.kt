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

    fun getAdbDevices(serialNumber: String): AndroidDevice? = androidDeviceMap[serialNumber]

    var serial: String = ""

    fun setChoosedDevice(serialNumber: String) {
        serial = serialNumber
    }

    suspend fun isAdbServerStarted(): Boolean {
        return platformAdapter.executeCommandWithResult("adb devices").contains("List of devices attached")
    }

    /**
     * 解析adb devices输出获取设备列表
     */
    private fun parseAdbDevicesOutput(output: String): List<String> {
        return output.lines()
            .asSequence()
            .filter { it.isNotBlank() }
            .drop(1) // 跳过"List of devices attached"标题行
            .map { line ->
                // 分割设备序列号和状态（如"device"、"offline"等）
                val parts = line.split("\\s+".toRegex())
                if (parts.isNotEmpty()) parts[0] else ""
            }
            .filter { it.isNotBlank() }
            .toList()
    }

    /**
     * 获取当前连接的设备列表
     */
    suspend fun getAdbDevicesList(): List<String> {
        val output = platformAdapter.executeCommandWithResult("adb devices")
        return parseAdbDevicesOutput(output)
    }

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
        androidDeviceMap.clear()

        devices.forEachIndexed { index, serialNumber ->
            // 创建设备实例并添加到映射
            // 获取设备名称
            val deviceName =
                platformAdapter.executeCommandWithResult("adb -s $serialNumber shell getprop ro.product.model")
                    .replace(Regex("\\r?\\n"), "")
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
    suspend fun getExecuteResult(serial: String, command: String, isRemoveReturn: Boolean = true): String {
        var result = ""
        androidDeviceMap[serial]?.excuteCommandWithResult(command)?.collect {
            result += if (isRemoveReturn)
                it.replace(Regex("\\r?\\n"), "")
            else
                it
        }
        return result
    }
}