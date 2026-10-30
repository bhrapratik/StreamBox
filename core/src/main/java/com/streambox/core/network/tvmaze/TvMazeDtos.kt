package com.streambox.core.network.tvmaze

import kotlinx.serialization.Serializable

@Serializable
data class TvMazeEpisodeDto(
    val id: Int,
    val name: String? = null,
    val airtime: String? = null,      // "21:00"
    val airstamp: String? = null,     // ISO-8601 with offset
    val runtime: Int? = null,
    val summary: String? = null,      // HTML!
    val show: TvMazeShowDto,
)

@Serializable
data class TvMazeShowDto(
    val id: Int,
    val name: String,
    val summary: String? = null,
    val image: TvMazeImageDto? = null,
    val network: TvMazeNetworkDto? = null,
)

@Serializable
data class TvMazeImageDto(val medium: String? = null, val original: String? = null)

@Serializable
data class TvMazeNetworkDto(val name: String)