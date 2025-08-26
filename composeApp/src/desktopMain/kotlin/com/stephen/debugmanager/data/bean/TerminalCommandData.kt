package com.stephen.debugmanager.data.bean

data class TerminalCommandData(
    val contents: String,
    val type: CommandType,
){
    enum class CommandType {
        USER,
        SYSTEM
    }
}
