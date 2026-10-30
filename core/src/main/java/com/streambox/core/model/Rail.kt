package com.streambox.core.model
enum class RailStyle { Poster, Landscape, Logo }

data class Rail(
    val id: String,
    val title: String,
    val items: List<Content>,
    val style: RailStyle = RailStyle.Poster,
)