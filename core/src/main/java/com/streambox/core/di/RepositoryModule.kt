package com.streambox.core.di

import com.streambox.core.ui.home.HomeRepository
import com.streambox.core.ui.home.HomeRepositoryImpl
import com.streambox.core.ui.home.detail.DetailsRepository
import com.streambox.core.ui.home.detail.DetailsRepositoryImpl
import com.streambox.core.ui.search.SearchRepository
import com.streambox.core.ui.search.SearchRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        homeRepositoryImpl: HomeRepositoryImpl,
    ): HomeRepository


    @Binds
    @Singleton
    abstract fun bindDetailsRepository(
        detailsRepositoryImpl: DetailsRepositoryImpl,
    ): DetailsRepository

    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        searchRepositoryImpl: SearchRepositoryImpl,
    ): SearchRepository
}
