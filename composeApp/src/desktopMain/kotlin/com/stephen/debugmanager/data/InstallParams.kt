package com.stephen.debugmanager.data

import com.stephen.composeapp.generated.resources.Res
import com.stephen.composeapp.generated.resources.app_manage_page_downgrade
import com.stephen.composeapp.generated.resources.app_manage_page_grant_permission
import com.stephen.composeapp.generated.resources.app_manage_page_install_directly
import com.stephen.composeapp.generated.resources.app_manage_page_reinstall
import com.stephen.composeapp.generated.resources.app_manage_page_test_install

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
    InstallParams.DEFAULT.param to Res.string.app_manage_page_install_directly,
    InstallParams.REINSTALL.param to Res.string.app_manage_page_reinstall,
    InstallParams.TEST.param to  Res.string.app_manage_page_test_install,
    InstallParams.DOWNGRADE.param to  Res.string.app_manage_page_downgrade,
    InstallParams.GRANT.param to  Res.string.app_manage_page_grant_permission
)
