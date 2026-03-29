package com.example.stillpoint.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

enum class ReaderTheme { LIGHT, DARK, SEPIA, SYSTEM }
enum class FontType { SANS_SERIF, SERIF, MONOSPACE }
enum class AppTheme { LIGHT, DARK, SYSTEM }

data class ReaderSettings(
    val fontSize: Int,
    val fontType: FontType,
    val theme: ReaderTheme
)

interface UserPreferencesRepository {
    val userName: Flow<String>
    val readerSettings: Flow<ReaderSettings>
    val appTheme: Flow<AppTheme>
    suspend fun updateUserName(name: String)
    suspend fun updateFontSize(size: Int)
    suspend fun updateFontType(type: FontType)
    suspend fun updateReaderTheme(theme: ReaderTheme)
    suspend fun updateAppTheme(theme: AppTheme)
}

class DataStoreUserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val FONT_SIZE = intPreferencesKey("reader_font_size")
        val FONT_TYPE = stringPreferencesKey("reader_font_type")
        val READER_THEME = stringPreferencesKey("reader_theme")
        val APP_THEME = stringPreferencesKey("app_theme")
    }

    override val userName: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.USER_NAME] ?: "User"
        }

    override val readerSettings: Flow<ReaderSettings> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            ReaderSettings(
                fontSize = preferences[PreferencesKeys.FONT_SIZE] ?: 18,
                fontType = FontType.valueOf(preferences[PreferencesKeys.FONT_TYPE] ?: FontType.SANS_SERIF.name),
                theme = ReaderTheme.valueOf(preferences[PreferencesKeys.READER_THEME] ?: ReaderTheme.SYSTEM.name)
            )
        }

    override val appTheme: Flow<AppTheme> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            AppTheme.valueOf(preferences[PreferencesKeys.APP_THEME] ?: AppTheme.SYSTEM.name)
        }

    override suspend fun updateUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
        }
    }

    override suspend fun updateFontSize(size: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.FONT_SIZE] = size.coerceIn(12, 32)
        }
    }

    override suspend fun updateFontType(type: FontType) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.FONT_TYPE] = type.name
        }
    }

    override suspend fun updateReaderTheme(theme: ReaderTheme) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.READER_THEME] = theme.name
        }
    }

    override suspend fun updateAppTheme(theme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_THEME] = theme.name
        }
    }
}
