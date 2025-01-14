package com.stephen.debugmanager.utils

import com.stephen.debugmanager.base.PlatformAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat

object LogUtils {

    private val logFileName = "Log_" + SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()) + ".txt"

    private val printScope = CoroutineScope(Dispatchers.Default)

    fun printLog(msg: String, level: LogLevel = LogLevel.DEBUG) {
        printScope.launch {
            writeToFile("${getTimeStamp()} $level $msg\n")
        }
    }

    private fun getTimeStamp(): String {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return dateFormat.format(currentTime)
    }

    // 新增方法，用于将字符串写入文件
    private fun writeToFile(msg: String) {
        synchronized(this) {
            printScope.launch {
                val file = File(PlatformAdapter.userLogConfigFile, logFileName)
                if (!file.exists()) {
                    file.createNewFile()
                }
                FileWriter(file, true).use { writer ->
                    writer.write(msg)
                }
            }
        }
    }

    enum class LogLevel {
        DEBUG,
        INFO,
        WARN,
        ERROR
    }
}