package com.diskin.alon.newsreader.util

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.test.espresso.IdlingRegistry
import androidx.test.runner.AndroidJUnitRunner
import com.diskin.alon.newsreader.home.presentation.MainActivity
import com.diskin.alon.newsreader.news.presentation.R
import dagger.hilt.android.testing.HiltTestApplication

class CustomTestRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        val app =  super.newApplication(cl, HiltTestApplication::class.java.name, context)

        // Register loading idling resource
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            private lateinit var loadingIdlingResource: LoadingIdlingResource

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if(activity is MainActivity) {
                    loadingIdlingResource = LoadingIdlingResource(
                        activity as FragmentActivity,
                        R.id.swipe_refresh
                    )
                    IdlingRegistry.getInstance().register(loadingIdlingResource)
                }
            }

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {
                if(activity is MainActivity) {
                    IdlingRegistry.getInstance().unregister(loadingIdlingResource)
                }
            }
        })

        return app
    }

    override fun onStart() {
        // Start test server
        NetworkUtil.initServer()
        super.onStart()
    }

    override fun onDestroy() {
        NetworkUtil.server.shutdown()
        super.onDestroy()
    }
}