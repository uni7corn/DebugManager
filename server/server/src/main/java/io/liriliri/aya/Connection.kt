package io.liriliri.aya

import android.annotation.TargetApi
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.net.LocalSocket
import android.os.Build
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import java.io.PrintWriter

class Connection(private val client: LocalSocket) : Thread() {
    private companion object {
        private const val TAG = "Aya.Connection"
        private const val ICON_CACHE_DIR = "/data/local/tmp/aya/icons"
        private const val APP_INFO_CACHE_DIR = "/data/local/tmp/aya/appInfo"

        init {
            val iconCacheDir = File(ICON_CACHE_DIR)
            if (!iconCacheDir.exists()) {
                iconCacheDir.mkdirs()
            }
            val appInfoDir = File(APP_INFO_CACHE_DIR)
            if (!appInfoDir.exists()) {
                appInfoDir.mkdirs()
            }
        }
    }

    override fun run() {
        val reader = BufferedReader(InputStreamReader(client.inputStream))
        val writer = PrintWriter(client.outputStream, true)

        while (!isInterrupted && client.isConnected) {
            try {
                val requestJson = reader.readLine() ?: break
                val request = JSONObject(requestJson)
                handleRequest(request, writer)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to handle request", e)
                break
            }
        }

        client.close()
        Log.i(TAG, "Client disconnected")
    }

    private fun handleRequest(request: JSONObject, writer: PrintWriter) {
        val method = request.getString("method")
        val params = request.optString("params", "{}")
        val id = request.getString("id")

        Log.i(TAG, "Request method: $method, params: $params")

        val response = JSONObject().apply {
            put("id", id)

            when (method) {
                "getVersion" -> {
                    put("version", getVersion())
                }

                "getPackageInfos" -> {
                    put("packageInfos", getPackageInfos(JSONObject(params)))
                }

                "saveAllInfoToFile" -> {
                    put("saveResult", saveAllInfoToFile(params))
                }

                else -> {
                    Log.e(TAG, "Unknown method: $method")
                    put("error", "Unknown method: $method")
                }
            }
        }

        Log.i(TAG, "Response: $response")
        writer.println(response.toString())
    }

    private fun saveAllInfoToFile(params: String): Boolean {
        Log.d(TAG, "====>saveAllInfoToFile<=====")
        // 5. 将 JSON 数据写入文件
        val jsonFilePath = "$APP_INFO_CACHE_DIR/apps_info_test.json"
        val jsonFile = File(jsonFilePath)
        try {
            val appListJson = getPackageInfos(JSONObject(params)).toString(4)
            jsonFile.writeText(appListJson)
            Log.d(TAG, "all app info saved to " + jsonFile.absolutePath)
        } catch (e: Exception) {
            Log.e(TAG, "error occured when saving, ${e.message}")
            return false
        }
        return true
    }

    private fun getVersion(): String {
        return BuildConfig.VERSION_NAME
    }

    private fun getPackageInfos(params: JSONObject): JSONArray {
        Log.i(TAG, "=========>getPackageInfos<==========")
        val packageNames = Util.jsonArrayToStringArray(params.getJSONArray("packageNames"))
        val result = JSONArray()

        packageNames.forEach {
            try {
                result.put(getPackageInfo(it))
            } catch (e: Exception) {
                Log.e(TAG, "Fail to get package info", e)
            }
        }

        Log.i(TAG, "getPackageInfos size: ${result.length()}")
        return result
    }

    @TargetApi(Build.VERSION_CODES.P)
    private fun getPackageInfo(packageName: String): JSONObject {
        var flags = PackageManager.GET_ACTIVITIES
        flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            flags or PackageManager.GET_SIGNING_CERTIFICATES
        } else {
            flags or PackageManager.GET_SIGNATURES
        }

        val packageInfo =
            ServiceManager.packageManager.getPackageInfo(packageName, flags)

        val info = JSONObject()
        info.put("packageName", packageInfo.packageName)
        info.put("versionName", packageInfo.versionName)
        info.put("firstInstallTime", packageInfo.firstInstallTime)
        info.put("lastUpdateTime", packageInfo.lastUpdateTime)
//        info.put("signatures", getSignatures(packageInfo))

        val applicationInfo = packageInfo.applicationInfo
        var apkSize = 0L
        val apkPath = applicationInfo.sourceDir
        apkSize = File(apkPath).length()
        info.put("apkPath", apkPath)
        info.put("apkSize", apkSize)
        info.put("enabled", applicationInfo.enabled)

        val system =
            ((applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM)

        info.put("system", system)

        var label = packageName

        // getLabel
        val resources = getResources(apkPath)
        val labelRes = applicationInfo.labelRes
        if (labelRes != 0) {
            try {
                label = resources.getString(labelRes)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get label for $packageName")
            }
        }
        info.put("label", label)

        if (applicationInfo.icon != 0) {
            try {
                val iconCachePath = "$ICON_CACHE_DIR/$packageName.png"
                val file = File(iconCachePath)
                if (file.exists()) {
                    Log.i(TAG, "icon is exist already")
                } else {
                    val resIcon = resources.getDrawable(applicationInfo.icon)
                    val bitmapIcon = Util.drawableToBitmap(resIcon)
                    val pngIcon = Util.bitMapToPng(bitmapIcon, 20)
                    file.writeBytes(pngIcon)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get icon for $packageName")
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            info.put("minSdkVersion", applicationInfo.minSdkVersion)
            info.put("targetSdkVersion", applicationInfo.targetSdkVersion)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val stats = ServiceManager.storageStatsManager.queryStatsForPackage(
                    packageName
                )
                info.put("appSize", stats.appBytes)
                info.put("dataSize", stats.dataBytes)
                info.put("cacheSize", stats.cacheBytes)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get storage stats for $packageName")
            }
        }

        Log.i(TAG, "infomation: $info")
        return info
    }

    private fun getResources(apkPath: String): Resources {
        val assetManager = AssetManager::class.java.newInstance() as AssetManager
        val addAssetManagerMethod =
            assetManager.javaClass.getMethod("addAssetPath", String::class.java)
        addAssetManagerMethod.invoke(assetManager, apkPath)

        val displayMetrics = DisplayMetrics()
        displayMetrics.setToDefaults()
        val configuration = Configuration()
        configuration.setToDefaults()

        return Resources(assetManager, displayMetrics, configuration)
    }

    private fun getSignatures(packageInfo: PackageInfo): JSONArray {
        val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.signingInfo.apkContentsSigners
        } else {
            packageInfo.signatures
        }

        val array = JSONArray()
        signatures.forEach {
            array.put(Base64.encodeToString(it.toByteArray(), Base64.NO_WRAP))
        }
        return array
    }
}
