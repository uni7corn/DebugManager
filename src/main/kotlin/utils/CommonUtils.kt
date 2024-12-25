package utils

import java.text.DecimalFormat
import java.text.SimpleDateFormat

/**
 * 获取日期字符串
 */
fun getDateString(): String = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(System.currentTimeMillis())

/**
 * 将文件大小显示为GB,MB等形式
 * 传入的数值默认为Byte
 */
fun Long.size(): String {
    return if (this / (1024 * 1024 * 1024) > 0) {
        val tmpthis = this.toFloat() / (1024 * 1024 * 1024).toFloat()
        val df = DecimalFormat("#.##")
        df.format(tmpthis.toDouble()) + "GB"
    } else if (this / (1024 * 1024) > 0) {
        val tmpthis = this.toFloat() / (1024 * 1024).toFloat()
        val df = DecimalFormat("#.##")
        df.format(tmpthis.toDouble()) + "MB"
    } else if (this / 1024 > 0) {
        (this / 1024).toString() + "KB"
    } else this.toString() + "B"
}
