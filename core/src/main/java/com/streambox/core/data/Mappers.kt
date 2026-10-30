package com.streambox.core.data

import com.streambox.core.model.CastMember
import com.streambox.core.model.Content
import com.streambox.core.model.ContentDetails
import com.streambox.core.model.ContentType
import com.streambox.core.network.tmdb.TmdbDetailsDto
import com.streambox.core.network.tmdb.TmdbImages
import com.streambox.core.network.tmdb.TmdbMediaDto
import com.streambox.core.network.tmdb.TmdbProviderDto
import com.streambox.core.network.tvmaze.TvMazeEpisodeDto

internal fun TmdbMediaDto.toContent(fallbackType: ContentType): Content? {
    val type = when (mediaType) {
        "movie" -> ContentType.Movie
        "tv" -> ContentType.Series
        null -> fallbackType
        else -> return null                  // "person" appears in /trending/all
    }
    val title = title ?: name ?: return null
    val prefix = if (type == ContentType.Movie) "movie" else "tv"
    return Content(
        id = "$prefix:$id",
        type = type,
        title = title,
        description = overview,
        posterUrl = TmdbImages.poster(posterPath),
        backdropUrl = TmdbImages.backdrop(backdropPath),
        year = (releaseDate ?: firstAirDate)?.take(4)?.takeIf { it.isNotBlank() },
        rating = voteAverage,
    )
}

internal fun TmdbProviderDto.toContent() = Content(
    id = "provider:$providerId",
    type = ContentType.Provider,
    title = providerName,
    posterUrl = TmdbImages.logo(logoPath),
)

internal fun TvMazeEpisodeDto.toContent() = Content(
    id = "tvmaze:$id",
    type = ContentType.Channel,
    title = show.name,
    description = (summary ?: show.summary).orEmpty().stripHtml(),
    posterUrl = show.image?.medium,
    backdropUrl = show.image?.original,
    badge = listOfNotNull(airtime, show.network?.name).joinToString(" · "),
)

internal fun TmdbDetailsDto.toDetails(contentId: String, type: ContentType) = ContentDetails(
    id = contentId,
    type = type,
    title = title ?: name.orEmpty(),
    overview = overview,
    posterUrl = TmdbImages.poster(posterPath),
    backdropUrl = TmdbImages.backdrop(backdropPath),
    year = (releaseDate ?: firstAirDate)?.take(4)?.takeIf { it.isNotBlank() },
    runtimeMinutes = runtime,
    seasons = numberOfSeasons,
    genres = genres.map { it.name },
    rating = voteAverage,
    cast = credits?.cast.orEmpty()
        .distinctBy { it.id }                 // same actor can appear twice → duplicate keys
        .take(15)
        .map { CastMember(it.id, it.name, it.character, TmdbImages.profile(it.profilePath)) },
)

private fun String.stripHtml() = replace(Regex("<[^>]*>"), "").trim()