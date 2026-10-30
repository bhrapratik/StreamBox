package com.streambox.core.ui.home

import com.streambox.core.model.HomeFeed

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Error(val message: String) : HomeUiState
    data class Success(val feed: HomeFeed) : HomeUiState
}