package com.stephen.debugmanager.data.uistate

import se.vidstige.jadb.RemoteFile

data class DirectoryState(
    val deviceCode:String? = null,
    val currentdirectory: String? = null,
    val subdirectories: List<RemoteFile> = listOf(),
) {
    fun toUiState() = DirectoryState(deviceCode = deviceCode, currentdirectory = currentdirectory, subdirectories = subdirectories)
}