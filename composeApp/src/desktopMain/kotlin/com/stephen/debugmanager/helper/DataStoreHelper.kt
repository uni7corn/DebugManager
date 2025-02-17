package com.stephen.debugmanager.helper

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

class DataStoreHelper {

    lateinit var dataStore: DataStore<Preferences>

    fun init(path: String) {
        dataStore = createDataStore(path)
    }

    private fun createDataStore(path: String): DataStore<Preferences> {
        return PreferenceDataStoreFactory.createWithPath(
            corruptionHandler = null,
            migrations = emptyList(),
            produceFile = { path.toPath() }
        )
    }
}