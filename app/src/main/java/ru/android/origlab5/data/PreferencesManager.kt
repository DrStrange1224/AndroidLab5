package ru.android.origlab5.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("prefs")

class PreferencesManager(private val context: Context) {

    companion object {
        private val KEY_LAST_QUERY = stringPreferencesKey("last_query")
    }

    val lastSearchQuery: Flow<String?>
        get() = context.dataStore.data.map { prefs ->
            prefs[KEY_LAST_QUERY]
        }

    suspend fun saveLastQuery(query: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LAST_QUERY] = query
        }
    }

    suspend fun clearLastQuery() {
        context.dataStore.edit { it.remove(KEY_LAST_QUERY) }
    }
}