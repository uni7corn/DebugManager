import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

val debugManagerAppVersion = "1.0.0"

group = "org.stephen"
version = debugManagerAppVersion

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
    maven("https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(compose.desktop.currentOs)
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlinx.coroutines.swing)
    implementation(libs.kotlin.serialization)
    implementation(libs.vidstige.jadb)
    implementation(libs.koin)
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Dmg, TargetFormat.Deb)
            packageName = "DebugManager"
            packageVersion = debugManagerAppVersion
            description = "DebugManager"
            copyright = "C 2024 Stephen. All rights reserved."
            vendor = "Stephen Zhan"
            licenseFile.set(project.file("LICENSE"))
            modules("java.instrument", "jdk.unsupported")
            // 配置打包保留文件，不同平台下区分
            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))
            windows {
                includeAllModules = true
                packageVersion = debugManagerAppVersion
                msiPackageVersion = debugManagerAppVersion
                exePackageVersion = debugManagerAppVersion
                menu = true
                shortcut = true
                // 可自行选择安装目录
                dirChooser = true
                // 可单独为当前用户安装，不需要管理员权限
                perUserInstall = true
                // 设置图标
                iconFile.set(project.file("launcher/icon.ico"))
                upgradeUuid = "888888-8888-8888-8888-888888888888"
            }
            linux {
                packageVersion = debugManagerAppVersion
                debPackageVersion = debugManagerAppVersion
                rpmPackageVersion = debugManagerAppVersion
                iconFile.set(project.file("launcher/icon.png"))
            }
            macOS {
                bundleID = "org.stephen.debugmanager"
                includeAllModules = true
                packageVersion = debugManagerAppVersion
                dmgPackageVersion = debugManagerAppVersion
                pkgPackageVersion = debugManagerAppVersion
                // 显示在菜单栏、“关于”菜单项、停靠栏等中的应用程序名称
                dockName = "DebugManager"
                packageBuildVersion = debugManagerAppVersion
                dmgPackageBuildVersion = debugManagerAppVersion
                pkgPackageBuildVersion = debugManagerAppVersion
                // 设置图标
                iconFile.set(project.file("launcher/icon.icns"))
                entitlementsFile.set(project.file("default.entitlements"))
            }
        }
    }
}