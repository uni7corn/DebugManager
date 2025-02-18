package com.stephen.debugmanager.helper

import com.stephen.debugmanager.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.zip.GZIPInputStream

class LogFileFinder {

    /**
     * 处理日志文件
     * @param path 日志文件路径
     * @param textToFind 要查找的文本
     * @return 处理后的新日志文件路径
     */
    suspend fun processLogFilesByText(path: String, textToFind: String) = withContext(Dispatchers.IO) {
        LogUtils.printLog("processLogFilesByText -> start to process, path: $path, textToFind: $textToFind")
        val folder = File(path)
        unzipGzFilesInFolder(folder)
        val resultPath = analysisLogFile(folder, textToFind)
        resultPath
    }

    /**
     * analysis log file, find all qualified log, generate a new txt file
     */
    suspend fun analysisLogFile(folder: File, textToFind: String): String? = withContext(Dispatchers.IO) {
        val foundLines = mutableListOf<String>()
        // 遍历识别所有前缀带有.logcat 的文件，将其内容写入到同目录下的 txt 文件
        folder.listFiles()?.forEach { file ->
            if (file.isFile && file.name.startsWith("logcat.")) {
                file.forEachLine { line ->
                    if (line.contains(textToFind)) {
                        foundLines.add(line)
                    }
                }
            }
        }
        LogUtils.printLog("analysisLogFile -> 找到的匹配的词条数量: ${foundLines.size}")
        // 写回到同目录下的 txt 文件
        val ts = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(System.currentTimeMillis())
        val outputFilePath = "${folder.parent}/${ts}_${textToFind}_logs.txt"
        val outputFile = File(outputFilePath)
        FileWriter(outputFile).use { writer ->
            foundLines.forEach { line ->
                writer.write(line)
                writer.write("\n")
            }
        }
        outputFile.parent
    }

    /**
     * 解压指定文件夹下的所有 .gz 文件
     */
    suspend fun unzipGzFilesInFolder(folder: File) = withContext(Dispatchers.IO) {
        if (folder.isDirectory) {
            // 遍历识别所有后缀带有.gz 的文件，将其解压后删除源文件
            var gzFileCount = 0
            folder.listFiles()?.forEach { file ->
                if (file.isFile && file.name.endsWith(".gz")) {
                    gzFileCount++
                    unzipGzFile(file)
                    file.delete()
                }
            }
            LogUtils.printLog("unzipGzFilesInFolder -> gz file count: $gzFileCount")
        }
    }

    /**
     * 解压单个.gz 文件
     */
    private suspend fun unzipGzFile(gzFile: File) = withContext(Dispatchers.IO) {
        val outputFileName = gzFile.absolutePath.removeSuffix(".gz")
        val outputFile = File(outputFileName)

        GZIPInputStream(FileInputStream(gzFile)).use { gis ->
            FileOutputStream(outputFile).use { fos ->
                val buffer = ByteArray(1024)
                var length: Int
                while (gis.read(buffer).also { length = it } > 0) {
                    fos.write(buffer, 0, length)
                }
            }
        }
    }
}