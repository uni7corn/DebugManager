package com.stephen.debugmanager.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

object LogUtils {

    private val printScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val logger = LoggerFactory.getLogger(LogUtils::class.java)

    fun printLog(msg: String, level: LogLevel = LogLevel.DEBUG) {
        printScope.launch {
            when (level) {
                LogLevel.DEBUG -> logger.debug(msg)
                LogLevel.INFO -> logger.info(msg)
                LogLevel.WARN -> logger.warn(msg)
                LogLevel.ERROR -> logger.error(msg)
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