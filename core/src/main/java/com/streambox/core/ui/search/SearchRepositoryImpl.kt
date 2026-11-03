package com.streambox.core.ui.search


import com.streambox.core.data.toContent
import com.streambox.core.model.Content
import com.streambox.core.model.ContentType
import com.streambox.core.network.tmdb.TmdbApi
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val api: TmdbApi,
) : SearchRepository {

    override suspend fun search(query: String): List<Content> {
        return api.searchMulti(query).results
            .mapNotNull { it.toContent(ContentType.Movie) }   // drops "person" results
            .filter { it.posterUrl != null }                  // no blank tiles in the grid
    }
}