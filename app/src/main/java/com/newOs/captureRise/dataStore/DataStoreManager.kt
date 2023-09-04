package com.newOs.captureRise.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DataStoreManager private constructor(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_settings")

    private val _isAlarmOn = MutableStateFlow(false)
    val isAlarmOn: StateFlow<Boolean> = _isAlarmOn.asStateFlow()

    private val _desiredObject = MutableStateFlow("")
    val desiredObject: StateFlow<String> = _desiredObject.asStateFlow()

    init {
        _isAlarmOn.value = false
        observeDataStore()
    }

    private fun observeDataStore() {
        GlobalScope.launch {
            context.dataStore.data.catch { exception ->
                if (exception is Exception) { }
            }.collect { preferences ->
                _isAlarmOn.value = preferences[booleanPreferencesKey("isAlarmOn")] ?: false
                _desiredObject.value = preferences[stringPreferencesKey("desiredObject")] ?: ""
            }
        }
    }

    suspend fun setIsAlarmOn(isAlarmOn: Boolean) {
        context.dataStore.edit { preferences -> preferences[booleanPreferencesKey("isAlarmOn")] = isAlarmOn }
    }

    suspend fun setDesiredObject(desiredObject: String) {
        context.dataStore.edit { preferences -> preferences[stringPreferencesKey("desiredObject")] = desiredObject }
    }

    companion object {
        @Volatile
        private var INSTANCE: DataStoreManager? = null

        fun getInstance(context: Context): DataStoreManager = INSTANCE ?: synchronized(this) {
            INSTANCE ?: DataStoreManager(context).also { INSTANCE = it }
        }
    }

}