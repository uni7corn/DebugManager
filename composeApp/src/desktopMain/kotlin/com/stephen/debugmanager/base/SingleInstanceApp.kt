package com.stephen.debugmanager.base

import com.stephen.debugmanager.utils.LogUtils
import java.io.File
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import java.nio.channels.FileLock
import kotlin.system.exitProcess


class SingleInstanceApp {

    private var lock: FileLock? = null
    private var channel: FileChannel? = null

    fun initCheckFileLock() {
        LogUtils.printLog("initCheckFileLock")
        val file = File("app.lock")
        channel = RandomAccessFile(file, "rw").getChannel()
        lock = channel?.tryLock()
        if (lock == null) {
            LogUtils.printLog("Another instance is already running.", LogUtils.LogLevel.ERROR)
            exitProcess(1)
        }
        // 添加JVM关闭时的钩子，释放锁
        Runtime.getRuntime().addShutdownHook(Thread(Runnable {
            runCatching {
                lock?.let {
                    it.release()
                    channel?.close()
                    file.delete()
                }
            }.onFailure { e ->
                e.printStackTrace()
            }
        }))
    }
}