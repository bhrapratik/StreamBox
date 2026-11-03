package com.streambox.core.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
) : ViewModel() {

    var query by mutableStateOf("")
        private set

    fun onQueryChange(value: String) {
        query = value
    }


    val state: StateFlow<SearchUiState> = snapshotFlow { query.trim() }
        .debounce(400.milliseconds)
        .distinctUntilChanged()
        .flatMapLatest { q ->
            if (q.length < 2) flowOf(SearchUiState.Idle)
            else flow<SearchUiState> {
                emit(SearchUiState.Loading)
                emit(search(q))
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SearchUiState.Idle
        )

    private suspend fun search(q: String): SearchUiState = try {
        val items = searchRepository.search(q)
        if (items.isEmpty()) SearchUiState.Empty(q) else SearchUiState.Results(items)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        SearchUiState.Error(e.message ?: "Search failed")
    }
}
