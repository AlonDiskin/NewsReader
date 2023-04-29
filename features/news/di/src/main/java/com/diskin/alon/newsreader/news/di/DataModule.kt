package com.diskin.alon.newsreader.news.di

import com.diskin.alon.newsreader.news.application.interfaces.HeadlinesRepository
import com.diskin.alon.newsreader.news.data.implementation.HeadlinesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class DataModule {

    @ActivityRetainedScoped
    @Binds
    abstract fun bindHeadlinesRepository(repository: HeadlinesRepositoryImpl): HeadlinesRepository
}