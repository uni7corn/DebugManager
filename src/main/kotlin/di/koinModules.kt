package di

import MainStateHolder
import base.AdbClient
import base.PlatformAdapter
import model.AndroidAppHelper
import model.FileManager
import org.koin.dsl.module

val koinModules = module {
    single<MainStateHolder> {
        MainStateHolder(get(), get(), get(), get())
    }
    single<PlatformAdapter> {
        PlatformAdapter()
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
}