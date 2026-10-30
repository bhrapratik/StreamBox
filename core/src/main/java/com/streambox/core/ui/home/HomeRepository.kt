package com.streambox.core.ui.home

import com.streambox.core.model.HomeFeed
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
     fun getHomeFeed() : Flow<HomeFeed>
}