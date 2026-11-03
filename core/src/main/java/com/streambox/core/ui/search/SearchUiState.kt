package com.streambox.core.ui.search

import com.streambox.core.model.Content

sealed interface SearchUiState {
    data object Idle : SearchUiState                       // nothing typed yet
    data object Loading : SearchUiState
    data class Results(val items: List<Content>) : SearchUiState
    data class Empty(val query: String) : SearchUiState
    data class Error(val message: String) : SearchUiState
}