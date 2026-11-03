package com.streambox.core.ui.search

import com.streambox.core.model.Content

interface SearchRepository {
    suspend fun search(query: String): List<Content>
}