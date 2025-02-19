package com.stephen.debugmanager.data.uistate

data class ResumedPackage(
    val resumeApp: String = "",
) {
    fun toUiState() = ResumedPackage(resumeApp = resumeApp)
}