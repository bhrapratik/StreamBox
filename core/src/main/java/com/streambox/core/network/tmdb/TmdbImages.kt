package com.streambox.core.network.tmdb

object TmdbImages {
    private const val BASE = "https://image.tmdb.org/t/p/"

    fun poster(path: String?) = path?.let { "${BASE}w500$it" }
    fun backdrop(path: String?) = path?.let { "${BASE}w1280$it" }
    fun logo(path: String?) = path?.let { "${BASE}w300$it" }

    fun profile(path: String?) = path?.let { "${BASE}w185$it" }
}