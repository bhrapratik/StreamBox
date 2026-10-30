package com.streambox.core.network.tmdb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbPage<T>(
    val page: Int,
    val results: List<T>,
)

@Serializable
data class TmdbMediaDto(
    val id: Int,
    @SerialName("media_type") val mediaType: String? = null, // only on /trending
    val title: String? = null,          // movies
    val name: String? = null,           // tv
    val overview: String = "",
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("first_air_date") val firstAirDate: String? = null,
    @SerialName("vote_average") val voteAverage: Double? = null,
)

@Serializable
data class TmdbProvidersDto(
    val results: List<TmdbProviderDto>,
)

@Serializable
data class TmdbProviderDto(
    @SerialName("provider_id") val providerId: Int,
    @SerialName("provider_name") val providerName: String,
    @SerialName("logo_path") val logoPath: String? = null,
    @SerialName("display_priorities") val displayPriorities: Map<String, Int> = emptyMap(),
)

@Serializable
data class TmdbDetailsDto(
    val id: Int,
    val title: String? = null,                       // movie
    val name: String? = null,                        // tv
    val overview: String = "",
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("first_air_date") val firstAirDate: String? = null,
    val runtime: Int? = null,                        // movie
    @SerialName("number_of_seasons") val numberOfSeasons: Int? = null, // tv
    val genres: List<TmdbGenreDto> = emptyList(),
    @SerialName("vote_average") val voteAverage: Double? = null,
    val credits: TmdbCreditsDto? = null,             // via append_to_response=credits
)

@Serializable
data class TmdbGenreDto(val id: Int, val name: String)

@Serializable
data class TmdbCreditsDto(val cast: List<TmdbCastDto> = emptyList())

@Serializable
data class TmdbCastDto(
    val id: Int,
    val name: String,
    val character: String? = null,
    @SerialName("profile_path") val profilePath: String? = null,
)