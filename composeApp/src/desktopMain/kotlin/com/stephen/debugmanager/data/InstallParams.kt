package com.stephen.debugmanager.data

/**
 * 安装参数
 */
enum class InstallParams(val param: String) {
    DEFAULT(""),
    REINSTALL("-r"),
    TEST("-t"),
    DOWNGRADE("-d"),
    GRANT("-g")
}
