package com.streambox.core.ui.home.detail

import com.streambox.core.model.ContentDetails

sealed interface DetailsUiState {
    object Loading : DetailsUiState
    data class Success(val details: ContentDetails) : DetailsUiState
    data class Error(val message: String) : DetailsUiState
}