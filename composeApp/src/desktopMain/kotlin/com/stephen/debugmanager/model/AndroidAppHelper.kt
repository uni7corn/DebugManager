package com.stephen.debugmanager.model

import com.stephen.debugmanager.base.AdbClient
import com.stephen.debugmanager.base.PlatformAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import com.stephen.debugmanager.utils.LogUtils
import java.io.File

class AndroidAppHelper(private val adbClient: AdbClient, private val platformAdapter: PlatformAdapter) {

    init {
        println("AndroidAppHelper init")
    }

    /**
     * 安装AppInfoService
     * 有时候执行会返回一个1，不能用String.empty来判断，改为用返回值的长度来判断
     */
    private suspend fun checkAppInfoServiceInstallation() = withContext(Dispatchers.IO) {
        if (adbClient.getExecuteResult(adbClient.choosedDevicePosition, "pm list packages | grep appinfoservice")
                .apply { LogUtils.printLog("check AppInfoService Install result:$this") }
                .length < 10
        ) {
            val appInfoServiceApkPath = platformAdapter.getAppInfoServiceApkPath()
            LogUtils.printLog("installAppInfoService:$appInfoServiceApkPath")
            platformAdapter.executeTerminalCommand("${platformAdapter.localAdbPath} ${adbClient.serial} install $appInfoServiceApkPath")
            delay(4000L)
        }
    }


    /**
     * 拉起android图标存储服务
     */
    suspend fun tryToLaunchSaveAppInfoService() = withContext(Dispatchers.IO) {
        adbClient.runRootScript()
        adbClient.runRemountScript()
        // 检查apk是否安装，未安装则先安装
        checkAppInfoServiceInstallation()
        // 进程未起，先延时
        if (adbClient.getExecuteResult(adbClient.choosedDevicePosition, "ps -A | grep com.stephen.appinfoservice")
                .apply { LogUtils.printLog("check AppInfoService Process Running result:$this") }
                .length < 10
        ) {
            platformAdapter.executeTerminalCommand("${platformAdapter.localAdbPath} ${adbClient.serial} shell am start -n com.stephen.appinfoservice/.MainActivity")
            delay(3000L)
        }
    }

    suspend fun pullAppInfoToComputer() = withContext(Dispatchers.IO) {
        LogUtils.printLog("pullAppInfoToComputer")
        tryToLaunchSaveAppInfoService()
        val androidPath = "/storage/emulated/0/Android/data/com.stephen.appinfoservice/files"
        platformAdapter.executeTerminalCommand("${platformAdapter.localAdbPath} ${adbClient.serial} pull $androidPath ${PlatformAdapter.userAndroidTempFiles}")
        delay(3000L)
    }

    /**
     * 循环读取图标文件，直到存在为止
     */
    suspend fun getIconFile(packageName: String) = withContext(Dispatchers.IO) {
        val path =
            "${PlatformAdapter.userAndroidTempFiles}${PlatformAdapter.sp}files${PlatformAdapter.sp}$packageName.png"
        var file = File(path)
        while (!file.exists()) {
            delay(1000L)
            file = File(path)
        }
        file
    }

    suspend fun analyzeAppLabel() = withContext(Dispatchers.IO) {
        val path =
            "${PlatformAdapter.userAndroidTempFiles}${PlatformAdapter.sp}files${PlatformAdapter.sp}packageMap.txt"
        val packageLabelMap = mutableMapOf<String, String>()
        // 读取到文件为止
        var file = File(path)
        while (!file.exists()) {
            LogUtils.printLog("packageMap.txt is not exist!")
            delay(1000L)
            file = File(path)
        }
        file.readLines(Charsets.UTF_8).forEach { line ->
            val (packageName, label) = line.split("=")
            packageLabelMap[packageName] = label
        }
        packageLabelMap
    }

    /**
     * 卸载appinfoservice
     */
    fun uninstallAppInfoService() {
        LogUtils.printLog("uninstallAppInfoService")
        platformAdapter.executeTerminalCommand("${platformAdapter.localAdbPath} ${adbClient.serial} shell am force-stop com.stephen.appinfoservice")
        platformAdapter.executeTerminalCommand("${platformAdapter.localAdbPath} ${adbClient.serial} uninstall com.stephen.appinfoservice")
    }
}