package com.diskin.alon.newsreader

import com.diskin.alon.newsreader.home.presentation.AppNavHelper

class AppNavHelperImpl : AppNavHelper {
    override fun getAppNavGraph(): Int {
        return R.navigation.app_nav_graph
    }
}