package com.streambox.core.ui.home.detail

import com.streambox.core.data.toDetails
import com.streambox.core.model.ContentDetails
import com.streambox.core.model.ContentType
import com.streambox.core.network.tmdb.TmdbApi
import javax.inject.Inject

class DetailsRepositoryImpl @Inject constructor(
    private val api: TmdbApi
) : DetailsRepository {
    override suspend fun getDetails(contentId: String): ContentDetails {
        // "movie:603" → prefix = "movie", id = 603
        val prefix = contentId.substringBefore(":")
        val id = contentId.substringAfter(":").toIntOrNull() ?: error("Invalid id: $contentId")
        return when (prefix) {
            "movie" -> api.movieDetails(id).toDetails(contentId, ContentType.Movie)
            "tv" -> api.tvDetails(id).toDetails(contentId, ContentType.Series)
            else -> error("No details for $contentId")
        }
    }
}