package com.stephen.debugmanager.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AndroidDevice(val name: String, val serial: String) {

    fun excuteCommand(command: String) {
        // 执行命令
        Runtime.getRuntime().exec("adb -s $serial shell $command")
    }

    fun excuteCommandWithResult(command: String): Flow<String> = callbackFlow {
        // 执行命令
        val process = Runtime.getRuntime().exec("adb -s $serial shell $command")
        val inputStream = process.inputStream
        val outputStream = process.outputStream
        val errorStream = process.errorStream
        val buffer = ByteArray(1024)
        var length: Int
        var result = ""
        while (inputStream.read(buffer).also { length = it } != -1) {
            result += String(buffer, 0, length)
            trySend(result)
        }
        outputStream.close()
        errorStream.close()
        process.waitFor()
        close()
    }

    fun push(localPath: String, remotePath: String) {
        // 推送文件
        Runtime.getRuntime().exec("adb -s $serial push $localPath $remotePath")
    }
}