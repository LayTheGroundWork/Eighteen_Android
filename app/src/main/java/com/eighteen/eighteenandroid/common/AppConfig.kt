package com.eighteen.eighteenandroid.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

object AppConfig {
    private val Context.appConfigDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_config_preference")
    private val soundKey = booleanPreferencesKey("sound_key")
    suspend fun getSoundOption(context: Context) = runBlocking {
        getSoundOptionFlow(context)
    }.firstOrNull()

    suspend fun setSoundOption(context: Context, soundOption: Boolean) {
        context.appConfigDataStore.edit { preferences ->
            preferences[soundKey] = soundOption
        }
    }

    fun getSoundOptionFlow(context: Context) =
        context.appConfigDataStore.data.map { it[soundKey] ?: false }
}

