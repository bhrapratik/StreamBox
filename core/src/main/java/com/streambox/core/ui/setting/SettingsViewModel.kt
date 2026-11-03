package com.streambox.core.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.streambox.core.design.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
) : ViewModel() {
    /** null until DataStore has been read once → avoids flashing the wrong theme on start. */
    val theme: StateFlow<AppTheme?> = themeRepository.theme
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch { themeRepository.setTheme(theme) }
    }
}