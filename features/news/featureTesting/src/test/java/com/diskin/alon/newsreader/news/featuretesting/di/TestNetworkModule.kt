package com.diskin.alon.newsreader.news.featuretesting.di

import com.diskin.alon.newsreader.news.data.remote.NewsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.mockk.mockk
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestNetworkModule {

    @Singleton
    @Provides
    fun provideFeedlyApi(): NewsApi {
        return mockk()
    }
}