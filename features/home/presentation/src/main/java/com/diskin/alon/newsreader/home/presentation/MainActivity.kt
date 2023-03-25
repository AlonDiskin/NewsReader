package com.diskin.alon.newsreader.home.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.diskin.alon.newsreader.home.presentation.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var navHelper: AppNavHelper
    private lateinit var layout: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set layout binding
        layout = DataBindingUtil.setContentView(this,R.layout.activity_main)

        // Setup toolbar
        setSupportActionBar(layout.toolbar)

        // Setup navigation fragment
        if (savedInstanceState == null) {
            val host = NavHostFragment.create(navHelper.getAppNavGraph())
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, host)
                .setPrimaryNavigationFragment(host)
                .commitNow()
        }
    }
}