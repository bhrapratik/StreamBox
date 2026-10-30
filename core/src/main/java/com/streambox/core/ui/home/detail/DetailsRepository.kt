package com.streambox.core.ui.home.detail

import com.streambox.core.model.ContentDetails
import kotlinx.coroutines.flow.Flow

interface DetailsRepository {
    suspend fun getDetails(contentId: String): ContentDetails
}