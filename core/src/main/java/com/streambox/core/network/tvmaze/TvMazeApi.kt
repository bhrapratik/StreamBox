package com.streambox.core.network.tvmaze

import com.streambox.core.di.TvMazeClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TvMazeApi @Inject constructor(
    @TvMazeClient private val client: HttpClient,
) {
    suspend fun schedule(country: String = "SE", date: String): List<TvMazeEpisodeDto> =
        client.get("schedule") {
            parameter("country", country)
            parameter("date", date)
        }.body()
}