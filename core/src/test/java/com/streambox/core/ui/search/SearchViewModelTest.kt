package com.streambox.core.ui.search

import app.cash.turbine.test
import com.streambox.core.model.Content
import com.streambox.core.model.ContentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `short query stays Idle without hitting the repository`() = runTest {
        val repository = FakeSearchRepository()
        val viewModel = SearchViewModel(repository)

        viewModel.state.test {
            assertEquals(SearchUiState.Idle, awaitItem())

            viewModel.onQueryChange("a")
            dispatcher.scheduler.advanceUntilIdle()

            expectNoEvents()
            assertEquals(emptyList<String>(), repository.queries)
        }
    }

    @Test
    fun `typing a query emits Loading then Results`() = runTest {
        val item = content("Interstellar")
        val repository = FakeSearchRepository(result = listOf(item))
        val viewModel = SearchViewModel(repository)

        viewModel.state.test {
            assertEquals(SearchUiState.Idle, awaitItem())

            viewModel.onQueryChange("inter")
            dispatcher.scheduler.advanceUntilIdle()

            assertEquals(SearchUiState.Loading, awaitItem())
            assertEquals(SearchUiState.Results(listOf(item)), awaitItem())
            assertEquals(listOf("inter"), repository.queries)
        }
    }

    @Test
    fun `no results emits Empty with the query`() = runTest {
        val repository = FakeSearchRepository(result = emptyList())
        val viewModel = SearchViewModel(repository)

        viewModel.state.test {
            assertEquals(SearchUiState.Idle, awaitItem())

            viewModel.onQueryChange("zzz")
            dispatcher.scheduler.advanceUntilIdle()

            assertEquals(SearchUiState.Loading, awaitItem())
            assertEquals(SearchUiState.Empty("zzz"), awaitItem())
        }
    }

    @Test
    fun `repository failure emits Error`() = runTest {
        val repository = FakeSearchRepository(error = IllegalStateException("boom"))
        val viewModel = SearchViewModel(repository)

        viewModel.state.test {
            assertEquals(SearchUiState.Idle, awaitItem())

            viewModel.onQueryChange("inter")
            dispatcher.scheduler.advanceUntilIdle()

            assertEquals(SearchUiState.Loading, awaitItem())
            assertEquals(SearchUiState.Error("boom"), awaitItem())
        }
    }

    @Test
    fun `rapid typing only searches the debounced query`() = runTest {
        val item = content("Dune")
        val repository = FakeSearchRepository(result = listOf(item))
        val viewModel = SearchViewModel(repository)

        viewModel.state.test {
            assertEquals(SearchUiState.Idle, awaitItem())

            viewModel.onQueryChange("d")
            viewModel.onQueryChange("du")
            viewModel.onQueryChange("dun")
            viewModel.onQueryChange("dune")
            dispatcher.scheduler.advanceUntilIdle()

            assertEquals(SearchUiState.Loading, awaitItem())
            assertEquals(SearchUiState.Results(listOf(item)), awaitItem())
            assertEquals(listOf("dune"), repository.queries)
        }
    }

    private fun content(title: String) = Content(
        id = "movie:$title",
        type = ContentType.Movie,
        title = title,
        posterUrl = "https://example.com/$title.jpg",
    )

    private class FakeSearchRepository(
        private val result: List<Content> = emptyList(),
        private val error: Throwable? = null,
    ) : SearchRepository {
        val queries = mutableListOf<String>()

        override suspend fun search(query: String): List<Content> {
            queries += query
            delay(1) // force a real suspension so Loading isn't conflated away
            error?.let { throw it }
            return result
        }
    }
}
