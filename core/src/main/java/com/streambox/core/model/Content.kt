package com.streambox.core.model


enum class ContentType { Movie, Series, Channel, Provider }

data class Content(
    val id: String,              // "movie:603", "tv:1399", "tvmaze:12345"
    val type: ContentType,
    val title: String,
    val description: String = "",
    val posterUrl: String? = null,    // 2:3
    val backdropUrl: String? = null,  // 16:9
    val badge: String? = null,        // "21:00 · SVT1", "MÅN–TIS"
    val year: String? = null,
    val rating: Double? = null,
)