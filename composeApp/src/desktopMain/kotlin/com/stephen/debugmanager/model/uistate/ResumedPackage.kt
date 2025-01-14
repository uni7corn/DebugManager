package com.stephen.debugmanager.model.uistate

data class ResumedPackage(
    val resumeApp: String = "",
) {
    fun toUiState() = ResumedPackage(resumeApp = resumeApp)
}