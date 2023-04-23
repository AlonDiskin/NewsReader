package com.diskin.alon.newsreader.journey

import androidx.test.filters.LargeTest
import com.diskin.alon.newsreader.news.di.NewsNetworkingModule
import com.diskin.alon.newsreader.util.DeviceUtil
import com.mauriciotogneri.greencoffee.GreenCoffeeConfig
import com.mauriciotogneri.greencoffee.GreenCoffeeTest
import com.mauriciotogneri.greencoffee.Scenario
import com.mauriciotogneri.greencoffee.ScenarioConfig
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*

@HiltAndroidTest
@UninstallModules(NewsNetworkingModule::class)
@RunWith(Parameterized::class)
@LargeTest
class ShareHeadlineStepsRunner(scenario: ScenarioConfig) : GreenCoffeeTest(scenario)  {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun scenarios(): Iterable<ScenarioConfig> {
            return GreenCoffeeConfig()
                .withFeatureFromAssets("assets/feature/share_headline.feature")
                .scenarios()
        }
    }

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Test
    fun test() {
        start(ShareHeadlineSteps())
    }

    override fun afterScenarioEnds(scenario: Scenario?, locale: Locale?) {
        super.afterScenarioEnds(scenario, locale)
        DeviceUtil.pressBack()
    }
}