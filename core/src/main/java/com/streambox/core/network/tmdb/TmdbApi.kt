package com.streambox.core.network.tmdb

import com.streambox.core.di.TmdbClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TmdbApi @Inject constructor(
    @TmdbClient private val client: HttpClient,
) {
    suspend fun trending(
        window: String = "week",
        language: String = TmdbConfig.LANGUAGE,
    ): TmdbPage<TmdbMediaDto> =
        client.get("trending/all/$window") {        // no leading "/" → resolves against /3/
            parameter("language", language)
        }.body()

    suspend fun nowPlayingMovies(
        region: String = TmdbConfig.REGION,
        language: String = TmdbConfig.LANGUAGE,
    ): TmdbPage<TmdbMediaDto> =
        client.get("movie/now_playing") {
            parameter("region", region)
            parameter("language", language)
        }.body()

    suspend fun topRatedTv(
        language: String = TmdbConfig.LANGUAGE,
    ): TmdbPage<TmdbMediaDto> =
        client.get("tv/top_rated") { parameter("language", language) }.body()

    suspend fun discoverMovies(
        genres: String,
        region: String = TmdbConfig.REGION,
        sortBy: String = "popularity.desc",
        language: String = TmdbConfig.LANGUAGE,
    ): TmdbPage<TmdbMediaDto> =
        client.get("discover/movie") {
            parameter("with_genres", genres)
            parameter("watch_region", region)
            parameter("sort_by", sortBy)
            parameter("language", language)
        }.body()

    suspend fun watchProviders(
        region: String = TmdbConfig.REGION,
        language: String = TmdbConfig.LANGUAGE,
    ): TmdbProvidersDto =
        client.get("watch/providers/movie") {
            parameter("watch_region", region)
            parameter("language", language)
        }.body()


    suspend fun movieDetails(id: Int, language: String = TmdbConfig.LANGUAGE): TmdbDetailsDto =
        client.get("movie/$id") {
            parameter("language", language)
            parameter("append_to_response", "credits")
        }.body()

    suspend fun tvDetails(id: Int, language: String = TmdbConfig.LANGUAGE): TmdbDetailsDto =
        client.get("tv/$id") {
            parameter("language", language)
            parameter("append_to_response", "credits")
        }.body()
}
object TmdbConfig {
    const val REGION = "SE"
    const val LANGUAGE = "en-US"   // switch to "sv-SE" later; sv overviews are often empty
    const val GENRE_FAMILY = "10751"
}