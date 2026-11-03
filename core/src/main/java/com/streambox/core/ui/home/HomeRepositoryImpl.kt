package com.streambox.core.ui.home

import com.streambox.core.data.toContent
import com.streambox.core.model.Content
import com.streambox.core.model.ContentType
import com.streambox.core.model.HomeFeed
import com.streambox.core.model.Rail
import com.streambox.core.model.RailStyle
import com.streambox.core.network.tmdb.TmdbApi
import com.streambox.core.network.tmdb.TmdbConfig
import com.streambox.core.network.tvmaze.TvMazeApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val tmdb: TmdbApi,
    private val tvMaze: TvMazeApi,
) : HomeRepository {

    override fun getHomeFeed(): Flow<HomeFeed> = flow {
        val rails = mutableListOf<Rail>()

        // 1. Hero
        val hero = safe {
            tmdb.trending().results
                .mapNotNull { it.toContent(ContentType.Movie) }
                .filter { it.backdropUrl != null }
                .take(5)
        }.orEmpty()
        emit(HomeFeed(hero, rails.toList(), isComplete = false))

        // 2. Streaming services
        safe { providerRail() }?.let {
            rails += Rail("providers", "All streaming services", it, RailStyle.Logo)
        }
        emit(HomeFeed(hero, rails.toList(), isComplete = false))

        // 3. On TV
        safe { onTvNow() }?.let {
            rails += Rail("on_tv", "On TV", it, RailStyle.Landscape)
        }
        emit(HomeFeed(hero, rails.toList(), isComplete = false))

        // 4. New in
        safe {
            tmdb.nowPlayingMovies().results
                .mapNotNull { it.toContent(ContentType.Movie) }
        }?.let {
            rails += Rail("new", "New in", it, RailStyle.Poster)
        }
        emit(HomeFeed(hero, rails.toList(), isComplete = false))

        // 5. Top 10 series
        safe {
            tmdb.topRatedTv().results
                .take(10)
                .mapNotNull { it.toContent(ContentType.Series) }
        }?.let {
            rails += Rail("top10_tv", "Top 10 series this week", it, RailStyle.Poster)
        }
        emit(HomeFeed(hero, rails.toList(), isComplete = false))

        // 6. Kids & family
        safe {
            tmdb.discoverMovies(TmdbConfig.GENRE_FAMILY).results
                .mapNotNull { it.toContent(ContentType.Movie) }
        }?.let {
            rails += Rail("kids", "Kids & family", it, RailStyle.Poster)
        }

        // Done
        val finalRails = rails.filter { it.items.isNotEmpty() }
        if (hero.isEmpty() && finalRails.isEmpty()) {
            error("Could not load home feed")
        }
        emit(HomeFeed(hero, finalRails, isComplete = true))
    }

    private suspend fun providerRail(): List<Content> =
        tmdb.watchProviders().results
            .sortedBy { it.displayPriorities[TmdbConfig.REGION] ?: Int.MAX_VALUE }
            .distinctBy { it.providerName.substringBefore(" ").lowercase() }
            .take(12)
            .map { it.toContent() }

    private suspend fun onTvNow(): List<Content> {
        val now = LocalTime.now()
        return tvMaze.schedule(date = LocalDate.now().toString())
            .filter { episode ->
                val start = episode.airtime
                    ?.let { runCatching { LocalTime.parse(it) }.getOrNull() }
                    ?: return@filter false
                // started within the last hour, or starts later today
                start.isAfter(now.minusHours(1))
            }
            .distinctBy { it.show.id }
            .take(15)
            .map { it.toContent() }
    }

    /** One failing rail must not break the whole screen. */
    private inline fun <T> safe(block: () -> T): T? =
        try {
            block()
        } catch (e: CancellationException) {
            throw e // never swallow cancellation
        } catch (e: Exception) {
            null
        }
}