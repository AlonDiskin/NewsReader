package com.diskin.alon.newsreader.home.presentation

import androidx.annotation.NavigationRes

interface AppNavHelper {

    @NavigationRes
    fun getAppNavGraph(): Int
}