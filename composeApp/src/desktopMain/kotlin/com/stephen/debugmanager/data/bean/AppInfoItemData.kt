package com.stephen.debugmanager.data.bean

import kotlinx.serialization.Serializable

@Serializable
data class AppInfoItemData(
    val packageName: String,
    val versionName: String,
    val firstInstallTime: Long?,
    val lastUpdateTime: Long?,
    val apkPath: String,
    val apkSize: Long?,
    val enabled: Boolean,
    val system: Boolean,
    val label: String,
    val minSdkVersion: Int,
    val targetSdkVersion: Int
)