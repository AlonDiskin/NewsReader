package com.diskin.alon.newsreader.news.featuretesting.headlines_browser

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.diskin.alon.newsreader.news.data.remote.NewsApi
import com.diskin.alon.newsreader.news.di.NewsNetworkingModule
import com.mauriciotogneri.greencoffee.GreenCoffeeConfig
import com.mauriciotogneri.greencoffee.GreenCoffeeTest
import com.mauriciotogneri.greencoffee.ScenarioConfig
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltAndroidTest
@UninstallModules(NewsNetworkingModule::class)
@RunWith(ParameterizedRobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(application = HiltTestApplication::class,instrumentedPackages = ["androidx.loader.content"])
@MediumTest
class ArticleSourceOpenedStepsRunner(scenario: ScenarioConfig) : GreenCoffeeTest(scenario) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun data(): Collection<Array<Any>> {
            val res = ArrayList<Array<Any>>()
            val scenarioConfigs = GreenCoffeeConfig()
                .withFeatureFromAssets("feature/headlines_browser.feature")
                .withTags("@article-source-opened")
                .scenarios()

            for (scenarioConfig in scenarioConfigs) {
                res.add(arrayOf(scenarioConfig))
            }

            return res
        }
    }

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var newsApi: NewsApi

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test() {
        val testDispatcher = UnconfinedTestDispatcher()

        mockkStatic(Dispatchers::Default)
        every { Dispatchers.Default } returns testDispatcher
        every { Dispatchers.Default.fold<Any>(any(), any()) } answers { testDispatcher.fold(firstArg(), secondArg())}
        every { Dispatchers.Default.get<CoroutineContext.Element>(any()) } answers { testDispatcher[firstArg()] }

        hiltRule.inject()
        start(ArticleSourceOpenedSteps(newsApi))
    }
}