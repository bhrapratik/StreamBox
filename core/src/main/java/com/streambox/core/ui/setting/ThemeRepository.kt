package com.streambox.core.ui.setting

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.streambox.core.design.AppTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore by preferencesDataStore(name = "settings")
@Singleton
class ThemeRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val themeKey = stringPreferencesKey("app_theme")

    val theme: Flow<AppTheme> = context.settingsDataStore.data.map { prefs ->
        prefs[themeKey]
            ?.let { name -> runCatching { AppTheme.valueOf(name) }.getOrNull() }
            ?: AppTheme.Default
    }

    suspend fun setTheme(theme: AppTheme) {
        context.settingsDataStore.edit { it[themeKey] = theme.name }
    }
}