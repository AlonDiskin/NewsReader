package com.diskin.alon.newsreader.news.data.remote

import com.diskin.alon.newsreader.news.application.util.NewsFeatureError
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkErrorHandler @Inject constructor() {

    fun handle(error: Throwable): Throwable {
        return when (error) {
            // Retrofit calls that return the body type throw either IOException for
            // network failures, or HttpException for any non-2xx HTTP status codes.
            // This code reports all errors to the UI
            is IOException -> NewsFeatureError.DeviceConnectionError
            is HttpException -> NewsFeatureError.RemoteServerError
            else -> error
        }
    }
}