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

val installOptions = mapOf(
    InstallParams.DEFAULT.param to "直接安装",
    InstallParams.REINSTALL.param to "重新安装",
    InstallParams.TEST.param to "测试安装",
    InstallParams.DOWNGRADE.param to "降级安装",
    InstallParams.GRANT.param to "赋权安装"
)
