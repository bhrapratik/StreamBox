package com.streambox.core.model

data class HomeFeed(
    val hero: List<Content>,   // top carousel
    val rails: List<Rail>,
    val isComplete: Boolean = true,   // false while rails are still arriving
)