package com.diskin.alon.newsreader.di

import com.diskin.alon.newsreader.AppNavHelperImpl
import com.diskin.alon.newsreader.home.presentation.AppNavHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAppNavHelper(): AppNavHelper {
        return AppNavHelperImpl()
    }
}