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
        const val FILE_SEPARATOR = "/"
        const val SD_CARD = "SD_CARD"
        const val PRIV_APP = "PRIV_APP"
        const val LAST_FOLDER = ".."
    }

    private val sdcardPathList = listOf("/", "sdcard")
    private val privAppPathList = listOf("/", "system", "priv-app")

    // 当前所处的目录，每一级分布于列表中
    private val currentDirPath = mutableListOf(ROOT_DIR)

    fun getDirPath() = currentDirPath

    private val fileOperationType = FileOperationType.NONE

    /**
     * 更新当前目录路径
     * @param path 目录路径
     * ROOT_DIR 表示返回根目录
     * LAST_FOLDER 表示返回上一级目录
     * 其他情况表示进入子目录
     */
    fun setCurrentDirPath(path: String) {
        when (path) {
            ROOT_DIR -> {
                currentDirPath.clear()
                currentDirPath.add(ROOT_DIR)
            }

            LAST_FOLDER -> {
                if (currentDirPath.size > 1)
                    currentDirPath.removeLast()
            }

            SD_CARD -> {
                currentDirPath.clear()
                currentDirPath.addAll(sdcardPathList)
            }

            PRIV_APP -> {
                currentDirPath.clear()
                currentDirPath.addAll(privAppPathList)
            }

            else -> {
                currentDirPath.add(path)
            }
        }
    }

    /**
     * 更新当前目录文件列表
     */
    suspend fun updateCurrentFileList(): List<RemoteFile> {
        val files = mutableListOf<RemoteFile>()
        platformAdapter.executeCommandWithResult(
            "${platformAdapter.localAdbPath} -s ${adbClient.serial} shell ls ${
                getDirPath().joinToString(FILE_SEPARATOR) + FILE_SEPARATOR
            } -l"
        ).apply {
            this.split("\n").filter {
                it.isNotBlank() && !it.startsWith("total ")
            }.forEach {
                val remoteFile = parseLineOutput(it)
                remoteFile?.let { element -> files.add(element) }
            }
        }
        return files
    }

    /**
     * 粘贴文件或文件夹
     */
    fun pasteFileOrFolder() {
        val destinationPath = getDirPath().joinToString("/")
//        when (fileOperationType) {
//            FileOperationType.COPY -> {
//                copyFileOrFolder(originalFilePath, destinationPath)
//            }
//
//            FileOperationType.MOVE -> {
//                moveFileOrFolder(originalFilePath, destinationPath)
//            }
//
//            FileOperationType.NONE -> {
//                LogUtils.printLog("文件操作状态未设置")
//            }
//        }
    }

    /**
     * 解析ls命令输出的单行
     */
    fun parseLineOutput(line: String): RemoteFile? {

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
            RemoteFile(name, isDirectory)
        } else null
    }

    /**
     * 删除文件或文件夹
     */
    fun deleteFileOrFolder(path: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val deletePath = getDirPath().joinToString(FILE_SEPARATOR) + FILE_SEPARATOR + path
            LogUtils.printLog("deleteFileOrFolder: $deletePath")
            adbClient.getExecuteResult(
                adbClient.serial,
                "rm -r $deletePath"
            )
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
            adbClient.push(windowsPath, androidPath)
        }.onFailure { e ->
            LogUtils.printLog("推送文件失败：${e.message}", LogUtils.LogLevel.ERROR)
        }
    }

    /**
     * 拉取文件到Windows设备
     */
    fun pullFileFromAndroid(fileName: String) {
        val path = getDirPath().joinToString(FILE_SEPARATOR) + FILE_SEPARATOR + fileName
        platformAdapter.executeTerminalCommand("${platformAdapter.localAdbPath} -s ${adbClient.serial} pull $path ${PlatformAdapter.desktopTempFolder}")
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