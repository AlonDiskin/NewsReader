package com.diskin.alon.newsreader.di

import com.diskin.alon.newsreader.news.data.remote.NewsApi
import com.diskin.alon.newsreader.util.NetworkUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestWeatherNetworkingModule {

    @Singleton
    @Provides
    fun provideLogging(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BASIC

        return logging
    }

    @Singleton
    @Provides
    fun provideHttpClient(logging: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Singleton
    @Provides
    fun provideNewsApi(httpClient: OkHttpClient): NewsApi {
        return Retrofit.Builder()
            .baseUrl(NetworkUtil.url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
            .create(NewsApi::class.java)
    }
}