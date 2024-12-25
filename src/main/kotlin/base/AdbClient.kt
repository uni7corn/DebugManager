package base

import kotlinx.coroutines.*
import se.vidstige.jadb.JadbConnection
import utils.LogUtils

class AdbClient(private val platformAdapter: PlatformAdapter) {

    init {
        println("AdbClient init")
    }

    lateinit var jadbClient: JadbConnection
        private set

    fun init() {
        // start-server不能带串口参数，直接adb start-server
        // 使用带result的方法来执行，必须等adb成功启动之后，再进行下一步的client初始化
        MainScope().launch {
            platformAdapter.executeCommandWithResult("${platformAdapter.localAdbPath} start-server")
            jadbClient = JadbConnection()
        }
    }

    fun getAdbClient(position: Int) = if (::jadbClient.isInitialized) jadbClient.devices[position] else null

    var choosedDevicePosition = 0
        private set

    var serial: String = ""

    fun setChoosedDevice(position: Int) {
        choosedDevicePosition = position
        serial = if (jadbClient.devices.isNotEmpty()) "-s ${jadbClient.devices[position].serial}" else "null"
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
        while (!::jadbClient.isInitialized) {
            delay(1000L)
        }
        val deviceMap = mutableMapOf<String, String>()
        setChoosedDevice(0)
        LogUtils.printLog("Connected Device Number:${jadbClient.devices.size}")
        for (i in 0 until jadbClient.devices.size) {
            val deviceName = getExecuteResult(i, "getprop ro.product.model")
            deviceMap[i.toString()] = deviceName
        }
        deviceMap
    }

    /**
     * 获取$localAdbPath Shell执行结果
     */
    fun getExecuteResult(position: Int, command: String): String {
        synchronized(this) {
            return runCatching {
                if (jadbClient.devices.isNotEmpty())
                    jadbClient.devices[position].execute(command).reader().use {
                        it.readText().replace(Regex("\\r?\\n"), "")
                    }
                else "NULL"
            }.onFailure { e ->
                LogUtils.printLog("execute went wrong:${e.message}", LogUtils.LogLevel.ERROR)
            }.getOrDefault("执行出错")
        }
    }
}