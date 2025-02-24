package com.stephen.debugmanager.di

import com.stephen.debugmanager.MainStateHolder
import com.stephen.debugmanager.base.AdbClient
import com.stephen.debugmanager.base.PlatformAdapter
import com.stephen.debugmanager.base.SingleInstanceApp
import com.stephen.debugmanager.helper.DataStoreHelper
import com.stephen.debugmanager.helper.LogFileFinder
import com.stephen.debugmanager.helper.AndroidAppHelper
import com.stephen.debugmanager.helper.FileManager
import com.stephen.debugmanager.net.DeepSeekRepository
import com.stephen.debugmanager.net.KimiRepository
import com.stephen.debugmanager.net.KtorClient
import org.koin.dsl.module

val koinModules = module {
    single<MainStateHolder> {
        MainStateHolder(get(), get(), get(), get(), get(), get(), get(),get())
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
    factory { DataStoreHelper() }
    factory { LogFileFinder() }
    factory { KtorClient() }
    factory { KimiRepository(get()) }
    factory { DeepSeekRepository(get()) }
}