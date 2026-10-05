package pl.chemia.game.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "chemia_settings")

data class UserSettings(
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val reducedMotion: Boolean = false,
)

class SettingsRepository(private val context: Context) {
    private object Keys {
        val sound = booleanPreferencesKey("sound_enabled")
        val haptics = booleanPreferencesKey("haptics_enabled")
        val reducedMotion = booleanPreferencesKey("reduced_motion")
    }

    val settings: Flow<UserSettings> = context.settingsDataStore.data.map { prefs ->
        UserSettings(
            soundEnabled = prefs[Keys.sound] ?: true,
            hapticsEnabled = prefs[Keys.haptics] ?: true,
            reducedMotion = prefs[Keys.reducedMotion] ?: false,
        )
    }

    suspend fun setSound(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.sound] = enabled }
    }

    suspend fun setHaptics(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.haptics] = enabled }
    }

    suspend fun setReducedMotion(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.reducedMotion] = enabled }
    }
}
