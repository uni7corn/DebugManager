package com.stephen.debugmanager.di

import com.stephen.debugmanager.MainStateHolder
import com.stephen.debugmanager.base.AdbClient
import com.stephen.debugmanager.base.PlatformAdapter
import com.stephen.debugmanager.base.SingleInstanceApp
import com.stephen.debugmanager.model.AndroidAppHelper
import com.stephen.debugmanager.model.FileManager
import org.koin.dsl.module

val koinModules = module {
    single<MainStateHolder> {
        MainStateHolder(get(), get(), get(), get())
    }
    single<PlatformAdapter> {
        PlatformAdapter(get())
    }
    single<AdbClient> {
        AdbClient(get())
    }
    single<FileManager> {
        FileManager(get(), get())
    }
    single<AndroidAppHelper> {
        AndroidAppHelper(get(), get())
    }
    factory { SingleInstanceApp() }
}