package com.stephen.debugmanager.data.bean

data class CommandData(
    val contents: String,
    val type: CommandType,
)

enum class CommandType {
    USER,
    SYSTEM
}
