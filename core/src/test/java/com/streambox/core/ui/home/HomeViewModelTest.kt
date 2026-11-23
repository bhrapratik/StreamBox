package com.streambox.core.ui.home

import com.streambox.core.model.HomeFeed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

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
    fun `state goes from Loading to Success once the repository flow emits`() = runTest {
        val feed = HomeFeed(hero = emptyList(), rails = emptyList())
        val repository = FakeHomeRepository(feed)
        val viewModel = HomeViewModel(repository)

        assertTrue(viewModel.state.value is HomeUiState.Loading)

        dispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is HomeUiState.Success)
        assertEquals(feed, (state as HomeUiState.Success).feed)
    }

    private class FakeHomeRepository(private val feed: HomeFeed) : HomeRepository {
        override fun getHomeFeed(): Flow<HomeFeed> = flow {
            emit(feed)
        }
    }
}
