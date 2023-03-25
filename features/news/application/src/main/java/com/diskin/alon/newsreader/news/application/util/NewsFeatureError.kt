package com.diskin.alon.newsreader.news.application.util

sealed class NewsFeatureError : Throwable() {

    object DeviceConnectionError : NewsFeatureError()

    object RemoteServerError : NewsFeatureError()
}
