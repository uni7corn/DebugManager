package com.stephen.debugmanager.data.bean

import kotlinx.serialization.Serializable

@Serializable
data class PackageListParams(
    val packageNames: List<String>
)

@Serializable
data class AyaRequest(
    val id: String,
    val method: String,
    val params: PackageListParams
)

@Serializable
data class AyaResponse(
    val id: String,
    val packageInfos: List<PackageInfo>
)

@Serializable
data class PackageInfo(
    val apkPath: String,
    val apkSize: Int,
    val enabled: Boolean,
    val firstInstallTime: Long,
    val label: String,
    val lastUpdateTime: Long,
    val minSdkVersion: Int,
    val packageName: String,
    val system: Boolean,
    val targetSdkVersion: Int,
    val versionName: String
)