package com.stephen.debugmanager.helper

import com.stephen.debugmanager.base.AdbClient
import com.stephen.debugmanager.base.PlatformAdapter
import com.stephen.debugmanager.data.FileOperationType
import com.stephen.debugmanager.data.RemoteFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.stephen.debugmanager.utils.LogUtils
import kotlinx.coroutines.withContext
import java.io.File

class FileManager(private val adbClient: AdbClient, private val platformAdapter: PlatformAdapter) {

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

    fun parseLineOutput(line: String): RemoteFile {

        // Split the line by one or more spaces to get individual parts.
        val parts = line.split("\\s+".toRegex())

        // The first character of the first part indicates the file type.
        val fileType = parts[0][0]

        // A file is considered a directory if its type is 'd' (directory) or 'l' (symbolic link).
        // For a file manager's navigation purpose, links to directories act as directories.
        val isDirectory = fileType == 'd' || fileType == 'l'

        // The file name is located from the 8th part onwards.
        // Special handling is needed for symbolic links which include a "->" and a target path.
        val arrowIndex = parts.indexOf("->")

        val name: String = if (arrowIndex != -1) {
            // For a symbolic link, the name is just before the "->".
            parts.subList(7, arrowIndex).joinToString(" ")
        } else {
            // For a regular file or directory, the name is the last part.
            parts.subList(7, parts.size).joinToString(" ")
        }

        // Create a FileInfo object and add to the list.
        return if (name.isNotEmpty()) {
            RemoteFile(name, isDirectory, getDirPath().joinToString("/") + "/$name")
        } else {
            RemoteFile("", false, "")
        }
    }

    /**
     * 删除文件或文件夹
     */
    fun deleteFileOrFolder(path: String) {
        CoroutineScope(Dispatchers.IO).launch {
            LogUtils.printLog("deleteFileOrFolder: $path")
            adbClient.getExecuteResult(adbClient.serial, "rm -r $path")
        }
    }

    /**
     * 复制文件或文件夹
     */
    fun copyFileOrFolder(sourcePath: String, destinationPath: String) {
        CoroutineScope(Dispatchers.IO).launch {
            LogUtils.printLog("cp sourcePath: $sourcePath, destinationPath: $destinationPath")
            adbClient.getExecuteResult(adbClient.serial, "cp -r $sourcePath $destinationPath")
        }
    }

    /**
     * 移动文件或文件夹
     */
    fun moveFileOrFolder(sourcePath: String, destinationPath: String) {
        CoroutineScope(Dispatchers.IO).launch {
            LogUtils.printLog("mv sourcePath: $sourcePath, destinationPath: $destinationPath")
            adbClient.getExecuteResult(adbClient.serial, "mv $sourcePath $destinationPath")
        }
    }

    /**
     * 推送文件到Android设备
     */
    suspend fun pushFileToAndroid(windowsPath: String, androidPath: String) = withContext(Dispatchers.IO) {
        runCatching {
            adbClient.getAdbDevices(adbClient.serial)?.push(windowsPath, androidPath)
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
            adbClient.getExecuteResult(adbClient.serial, "mkdir $path")
        }
    }

    /**
     * 创建文件
     */
    fun createFile(content: String, path: String) {
        CoroutineScope(Dispatchers.IO).launch {
            adbClient.getExecuteResult(adbClient.serial, "echo \"$content\" > $path")
        }
    }
}