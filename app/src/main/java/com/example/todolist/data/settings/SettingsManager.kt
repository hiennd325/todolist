package com.example.todolist.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.todolist.ui.theme.ThemePreset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {

    companion object {
        val THEME_PRESET = stringPreferencesKey("theme_preset")
        val DARK_MODE = stringPreferencesKey("dark_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val FONT_SIZE = stringPreferencesKey("font_size")
        val CUSTOM_PRIMARY_COLOR = stringPreferencesKey("custom_primary_color")
        val CUSTOM_PRIMARY_DARK_COLOR = stringPreferencesKey("custom_primary_dark_color")
    }

    val settingsFlow: Flow<Settings> = context.dataStore.data.map { preferences ->
        Settings(
            themePreset = ThemePreset.fromString(preferences[THEME_PRESET] ?: "Default"),
            darkMode = preferences[DARK_MODE] ?: "system",
            dynamicColor = preferences[DYNAMIC_COLOR] ?: false,
            fontSize = preferences[FONT_SIZE] ?: "medium",
            customPrimaryColor = preferences[CUSTOM_PRIMARY_COLOR],
            customPrimaryDarkColor = preferences[CUSTOM_PRIMARY_DARK_COLOR]
        )
    }

    suspend fun updateThemePreset(preset: ThemePreset) {
        context.dataStore.edit { preferences ->
            preferences[THEME_PRESET] = ThemePreset.toString(preset)
        }
    }

    suspend fun updateDarkMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE] = mode
        }
    }

    suspend fun updateDynamicColor(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR] = enabled
        }
    }

    suspend fun updateFontSize(size: String) {
        context.dataStore.edit { preferences ->
            preferences[FONT_SIZE] = size
        }
    }

    suspend fun updateCustomColors(primaryColor: String?, primaryDarkColor: String?) {
        context.dataStore.edit { preferences ->
            if (primaryColor != null) {
                preferences[CUSTOM_PRIMARY_COLOR] = primaryColor
            } else {
                preferences.remove(CUSTOM_PRIMARY_COLOR)
            }
            if (primaryDarkColor != null) {
                preferences[CUSTOM_PRIMARY_DARK_COLOR] = primaryDarkColor
            } else {
                preferences.remove(CUSTOM_PRIMARY_DARK_COLOR)
            }
        }
    }

    data class Settings(
        val themePreset: ThemePreset,
        val darkMode: String,
        val dynamicColor: Boolean,
        val fontSize: String = "medium",
        val customPrimaryColor: String? = null,
        val customPrimaryDarkColor: String? = null
    )
}
