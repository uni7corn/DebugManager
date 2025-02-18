package com.stephen.debugmanager.model

import com.stephen.debugmanager.base.AdbClient
import com.stephen.debugmanager.base.PlatformAdapter
import com.stephen.debugmanager.data.FileOperationType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import se.vidstige.jadb.RemoteFile
import com.stephen.debugmanager.utils.LogUtils
import kotlinx.coroutines.withContext
import java.io.File

class FileManager(private val adbClient: AdbClient, private val platformAdapter: PlatformAdapter) {

    init {
        println("FileManager init")
    }

    companion object {
        const val ROOT_DIR = "/"
        const val SDCARD_DIR = "/sdcard"
        const val LAST_DIR = ".."
        const val PRIV_APP = "system/priv-app"
    }

    private val directoryList = mutableListOf(ROOT_DIR)

    fun prepareDirPath(path: String) {
        when (path) {
            ROOT_DIR, SDCARD_DIR, PRIV_APP -> {
                directoryList.clear()
                directoryList.add(path)
            }

            LAST_DIR -> {
                if (directoryList.size > 1)
                    directoryList.removeLast()
            }

            else -> {
                directoryList.add(path)
            }
        }
    }

    fun getDirPath() = directoryList

    /**
     * 设置需要操作的文件或文件夹
     */
    var selectedFilePath: String = ""
        private set

    fun setSelectedFilePath(path: String) {
        selectedFilePath = getDirPath().joinToString("/") + "/$path"
    }

    /**
     * 设置文件操作状态
     */
    private var fileOperationType: FileOperationType = FileOperationType.NONE
    private var originalFilePath: String = ""
    fun setFileOperationState(operationType: FileOperationType) {
        LogUtils.printLog("setFileOperationState: $operationType")
        fileOperationType = operationType
        originalFilePath = selectedFilePath
    }

    /**
     * 粘贴文件或文件夹
     */
    fun pasteFileOrFolder() {
        val destinationPath = getDirPath().joinToString("/")
        when (fileOperationType) {
            FileOperationType.COPY -> {
                copyFileOrFolder(originalFilePath, destinationPath)
            }

            FileOperationType.MOVE -> {
                moveFileOrFolder(originalFilePath, destinationPath)
            }

            FileOperationType.NONE -> {
                LogUtils.printLog("文件操作状态未设置")
            }
        }
    }


    /**
     * 删除文件或文件夹
     */
    fun deleteFileOrFolder(path: String) {
        CoroutineScope(Dispatchers.IO).launch {
            LogUtils.printLog("deleteFileOrFolder: $path")
            adbClient.getExecuteResult(adbClient.choosedDevicePosition, "rm -r $path")
        }
    }

    /**
     * 复制文件或文件夹
     */
    fun copyFileOrFolder(sourcePath: String, destinationPath: String) {
        CoroutineScope(Dispatchers.IO).launch {
            LogUtils.printLog("cp sourcePath: $sourcePath, destinationPath: $destinationPath")
            adbClient.getExecuteResult(adbClient.choosedDevicePosition, "cp -r $sourcePath $destinationPath")
        }
    }

    /**
     * 移动文件或文件夹
     */
    fun moveFileOrFolder(sourcePath: String, destinationPath: String) {
        CoroutineScope(Dispatchers.IO).launch {
            LogUtils.printLog("mv sourcePath: $sourcePath, destinationPath: $destinationPath")
            adbClient.getExecuteResult(adbClient.choosedDevicePosition, "mv $sourcePath $destinationPath")
        }
    }

    /**
     * 推送文件到Android设备
     */
    suspend fun pushFileToAndroid(windowsPath: String, androidPath: String) = withContext(Dispatchers.IO) {
        runCatching {
            adbClient.getAdbClient(adbClient.choosedDevicePosition)?.push(File(windowsPath), RemoteFile(androidPath))
        }.onFailure { e ->
            LogUtils.printLog("推送文件失败：${e.message}", LogUtils.LogLevel.ERROR)
        }
    }

    /**
     * 推送文件夹到Android设备
     */
    suspend fun pushFolderToAndroid(windowsPath: String, androidPath: String) {
        runCatching {
            // android端先创建文件夹
            createDirectory(androidPath)
            // windows端遍历文件夹，逐个推送文件
            val flder = File(windowsPath)
            if (flder.isDirectory) {
                flder.listFiles()?.forEach { file ->
                    if (file.isFile) {
                        pushFileToAndroid(file.absolutePath, "$androidPath/${file.name}")
                    } else {
                        pushFolderToAndroid(file.absolutePath, "$androidPath/${file.name}")
                    }
                }
            }
        }.onFailure { e ->
            LogUtils.printLog("推送文件失败：${e.message}", LogUtils.LogLevel.ERROR)
        }
    }

    /**
     * 拉取文件到Windows设备
     */
    fun pullFileFromAndroid(androidPath: String) {
        platformAdapter.executeTerminalCommand("${platformAdapter.localAdbPath} ${adbClient.serial} pull $androidPath ${PlatformAdapter.desktopTempFolder}")
    }

    /**
     * 创建文件夹
     */
    fun createDirectory(path: String) {
        CoroutineScope(Dispatchers.IO).launch {
            adbClient.getExecuteResult(adbClient.choosedDevicePosition, "mkdir $path")
        }
    }

    /**
     * 创建文件
     */
    fun createFile(content: String, path: String) {
        CoroutineScope(Dispatchers.IO).launch {
            adbClient.getExecuteResult(adbClient.choosedDevicePosition, "echo \"$content\" > $path")
        }
    }
}