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

    @Test
    fun `search waits for 400ms of silence`() = runTest {
        val repository = FakeSearchRepository(result = listOf(content("Dune")))
        val viewModel = SearchViewModel(repository)

        viewModel.state.test {
            assertEquals(SearchUiState.Idle, awaitItem())

            viewModel.onQueryChange("du")
            dispatcher.scheduler.runCurrent()

            dispatcher.scheduler.advanceTimeBy(399)
            dispatcher.scheduler.runCurrent()
            assertEquals(emptyList<String>(), repository.queries)

            dispatcher.scheduler.advanceTimeBy(2)
            dispatcher.scheduler.runCurrent()
            assertEquals(listOf("du"), repository.queries)

            assertEquals(SearchUiState.Loading, awaitItem())
            assertEquals(SearchUiState.Results(listOf(content("Dune"))), awaitItem())
        }
    }

    @Test
    fun `burst of typing searches only the last query`() = runTest {
        val repository = FakeSearchRepository(result = listOf(content("Dune")))
        val viewModel = SearchViewModel(repository)

        viewModel.state.test {
            assertEquals(SearchUiState.Idle, awaitItem())

            viewModel.onQueryChange("du")
            dispatcher.scheduler.runCurrent()
            dispatcher.scheduler.advanceTimeBy(100)
            dispatcher.scheduler.runCurrent()

            viewModel.onQueryChange("dun")
            dispatcher.scheduler.runCurrent()
            dispatcher.scheduler.advanceTimeBy(100)
            dispatcher.scheduler.runCurrent()

            viewModel.onQueryChange("dune")
            dispatcher.scheduler.runCurrent()
            dispatcher.scheduler.advanceTimeBy(401)
            dispatcher.scheduler.runCurrent()

            assertEquals(listOf("dune"), repository.queries)

            assertEquals(SearchUiState.Loading, awaitItem())
            assertEquals(SearchUiState.Results(listOf(content("Dune"))), awaitItem())
        }
    }

    @Test
    fun `retry on Error re-runs the failed query and succeeds`() = runTest {
        val repository = FakeSearchRepository(error = IllegalStateException("boom"))
        val viewModel = SearchViewModel(repository)

        viewModel.state.test {
            assertEquals(SearchUiState.Idle, awaitItem())

            viewModel.onQueryChange("inter")
            dispatcher.scheduler.advanceUntilIdle()

            assertEquals(SearchUiState.Loading, awaitItem())
            assertEquals(SearchUiState.Error("boom"), awaitItem())

            val item = content("Interstellar")
            repository.error = null
            repository.result = listOf(item)

            viewModel.retry()
            dispatcher.scheduler.advanceUntilIdle()

            assertEquals(SearchUiState.Loading, awaitItem())
            assertEquals(SearchUiState.Results(listOf(item)), awaitItem())
            assertEquals(listOf("inter", "inter"), repository.queries)
        }
    }

    @Test
    fun `retry is immediate, no debounce wait`() = runTest {
        val repository = FakeSearchRepository(error = IllegalStateException("boom"))
        val viewModel = SearchViewModel(repository)

        viewModel.state.test {
            assertEquals(SearchUiState.Idle, awaitItem())

            viewModel.onQueryChange("inter")
            dispatcher.scheduler.advanceUntilIdle()

            assertEquals(SearchUiState.Loading, awaitItem())
            assertEquals(SearchUiState.Error("boom"), awaitItem())

            viewModel.retry()
            dispatcher.scheduler.runCurrent()

            assertEquals(listOf("inter", "inter"), repository.queries)
            assertEquals(SearchUiState.Loading, awaitItem())

            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(SearchUiState.Error("boom"), awaitItem())
        }
    }

    @Test
    fun `retry no-ops outside Error state`() = runTest {
        val repository = FakeSearchRepository(result = listOf(content("Dune")))
        val viewModel = SearchViewModel(repository)

        viewModel.state.test {
            assertEquals(SearchUiState.Idle, awaitItem())

            // Idle
            viewModel.retry()
            dispatcher.scheduler.advanceUntilIdle()
            expectNoEvents()
            assertEquals(emptyList<String>(), repository.queries)

            viewModel.onQueryChange("dune")
            dispatcher.scheduler.runCurrent()
            assertEquals(SearchUiState.Loading, awaitItem())

            // Loading
            viewModel.retry()
            dispatcher.scheduler.runCurrent()
            expectNoEvents()
            assertEquals(listOf("dune"), repository.queries)

            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(SearchUiState.Results(listOf(content("Dune"))), awaitItem())

            // Results
            viewModel.retry()
            dispatcher.scheduler.advanceUntilIdle()
            expectNoEvents()
            assertEquals(listOf("dune"), repository.queries)
        }
    }

    @Test
    fun `retry uses the last searched query, not the current unsubmitted text`() = runTest {
        val repository = FakeSearchRepository(error = IllegalStateException("boom"))
        val viewModel = SearchViewModel(repository)

        viewModel.state.test {
            assertEquals(SearchUiState.Idle, awaitItem())

            viewModel.onQueryChange("inter")
            dispatcher.scheduler.advanceUntilIdle()

            assertEquals(SearchUiState.Loading, awaitItem())
            assertEquals(SearchUiState.Error("boom"), awaitItem())

            viewModel.onQueryChange("interst")
            dispatcher.scheduler.advanceTimeBy(100)
            dispatcher.scheduler.runCurrent()

            viewModel.retry()
            dispatcher.scheduler.runCurrent()

            // repository.queries already shows the retried "inter" call, even though
            // "interst" was typed afterward and its own 400ms debounce has not elapsed.
            assertEquals(listOf("inter", "inter"), repository.queries)
            assertEquals(SearchUiState.Loading, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a retry in flight is cancelled by a subsequent typed query`() = runTest {
        // Retargeted from "a second retry cancels the first": reaching the first
        // retry's search() flips state to Loading, which blocks a second retry()
        // at the req. 3 guard, and firing both retry() calls before any dispatcher
        // tick instead hits req. 2's extraBufferCapacity = 1, which drops the
        // second tryEmit before it ever reaches flatMapLatest. Neither path puts
        // two values through flatMapLatest. A typed query racing an in-flight
        // retry does, and still proves the shared-pipeline cancellation semantics
        // (flatMapLatest, not flatMapMerge/flatMapConcat, and one collector, not two).
        val repository = FakeSearchRepository(error = IllegalStateException("boom"), delayMs = 500)
        val viewModel = SearchViewModel(repository)

        viewModel.state.test {
            assertEquals(SearchUiState.Idle, awaitItem())

            viewModel.onQueryChange("inter")
            dispatcher.scheduler.advanceUntilIdle()

            assertEquals(SearchUiState.Loading, awaitItem())
            assertEquals(SearchUiState.Error("boom"), awaitItem())

            val retryResult = content("Interstellar")
            val typedResult = content("Intermission")
            repository.error = null
            repository.result = listOf(retryResult)

            viewModel.retry()
            dispatcher.scheduler.runCurrent()
            assertEquals(SearchUiState.Loading, awaitItem())

            // The retry's search() is now suspended on a 500ms delay. A typed query
            // arrives (its 400ms debounce resolves before the retry's 500ms delay
            // does), so flatMapLatest must cancel the still-suspended retry search.
            viewModel.onQueryChange("dune")
            repository.result = listOf(typedResult)
            dispatcher.scheduler.advanceUntilIdle()

            assertEquals(SearchUiState.Results(listOf(typedResult)), awaitItem())
            assertEquals(listOf("inter", "inter", "dune"), repository.queries)
        }
    }

    private fun content(title: String) = Content(
        id = "movie:$title",
        type = ContentType.Movie,
        title = title,
        posterUrl = "https://example.com/$title.jpg",
    )

    private class FakeSearchRepository(
        var result: List<Content> = emptyList(),
        var error: Throwable? = null,
        private val delayMs: Long = 1,
    ) : SearchRepository {
        val queries = mutableListOf<String>()

        override suspend fun search(query: String): List<Content> {
            queries += query
            // Capture before suspending: a call already in flight must not observe a
            // config mutation made after it started (otherwise a wrongly-uncancelled
            // call would silently agree with the call that should have won).
            val capturedResult = result
            val capturedError = error
            delay(delayMs) // force a real suspension so Loading isn't conflated away
            capturedError?.let { throw it }
            return capturedResult
        }
    }
}
