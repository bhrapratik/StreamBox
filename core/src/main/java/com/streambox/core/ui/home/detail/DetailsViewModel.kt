package com.streambox.core.ui.home.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: DetailsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    // Navigation puts route arguments into SavedStateHandle under the property name
    private val contentId: String =
        checkNotNull(savedStateHandle["contentId"]) { "contentId missing" }

    private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()


    init {
        load()
    }

    fun retry() = load()

    fun load() {
        _uiState.value = DetailsUiState.Loading
        viewModelScope.launch {
            _uiState.value = try {
                DetailsUiState.Success(repository.getDetails(contentId))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                DetailsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}