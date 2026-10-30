package com.streambox.core.model


data class ContentDetails(
    val id: String,
    val type: ContentType,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val year: String?,
    val runtimeMinutes: Int?,
    val seasons: Int?,
    val genres: List<String>,
    val rating: Double?,
    val cast: List<CastMember>,
)

data class CastMember(
    val id: Int,
    val name: String,
    val character: String?,
    val photoUrl: String?,
)

/** "2024 · 2h 16m · ★ 7.9" — shared by mobile and TV */
fun ContentDetails.metaLine(): String = listOfNotNull(
    year,
    runtimeMinutes?.takeIf { it > 0 }?.let { "${it / 60}h ${it % 60}m" },
    seasons?.let { if (it == 1) "1 season" else "$it seasons" },
    rating?.takeIf { it > 0 }?.let { "★ %.1f".format(it) },
).joinToString(" · ")

/** Only TMDB movies and series have a details page (not providers or TV schedule items). */
val Content.hasDetails: Boolean
    get() = type == ContentType.Movie || type == ContentType.Series