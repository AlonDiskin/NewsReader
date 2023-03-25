package com.diskin.alon.newsreader.news.featuretesting.headlines_browser

import android.os.Looper
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.contrib.RecyclerViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.newsreader.common.uitesting.HiltTestActivity
import com.diskin.alon.newsreader.common.uitesting.RecyclerViewMatcher.*
import com.diskin.alon.newsreader.common.uitesting.isRecyclerViewItemsCount
import com.diskin.alon.newsreader.common.uitesting.launchFragmentInHiltContainer
import com.diskin.alon.newsreader.common.uitesting.swipeToRefresh
import com.diskin.alon.newsreader.news.data.BuildConfig
import com.diskin.alon.newsreader.news.data.remote.*
import com.diskin.alon.newsreader.news.presentation.R
import com.diskin.alon.newsreader.news.presentation.model.UiHeadline
import com.diskin.alon.newsreader.news.presentation.ui.HeadlinesAdapter
import com.diskin.alon.newsreader.news.presentation.ui.HeadlinesFragment
import com.google.gson.Gson
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import io.mockk.clearAllMocks
import io.mockk.clearMocks
import io.mockk.coEvery
import org.joda.time.DateTime
import org.robolectric.Shadows
import java.io.File

class HeadlinesShownSteps(
    private val newsApi: NewsApi
)  : GreenCoffeeSteps() {

    private val scenario: ActivityScenario<HiltTestActivity>
    private val gson = Gson()

    init {
        // Stub remote api mock
        val pages = listOf(
            "json/news_api_headlines_page_1.json",
            "json/news_api_headlines_page_2.json",
            "json/news_api_headlines_last_page.json"
        )
        val firstHeadlinesPageResponse = gson.fromJson(getJsonFromResource(pages[0]), NewsApiResponse::class.java)
        val secondHeadlinesPageResponse = gson.fromJson(getJsonFromResource(pages[1]), NewsApiResponse::class.java)
        val lastHeadlinesPageResponse = gson.fromJson(getJsonFromResource(pages[2]), NewsApiResponse::class.java)

        coEvery { newsApi.getHeadlines(BuildConfig.NEWS_API_KEY, NEWS_API_SOURCES,1) } returns firstHeadlinesPageResponse
        coEvery { newsApi.getHeadlines(BuildConfig.NEWS_API_KEY, NEWS_API_SOURCES,2) } returns secondHeadlinesPageResponse
        coEvery { newsApi.getHeadlines(BuildConfig.NEWS_API_KEY, NEWS_API_SOURCES,3) } returns lastHeadlinesPageResponse

        // Launch headlines fragment
        scenario = launchFragmentInHiltContainer<HeadlinesFragment>()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Given("^user has browsed current headlines$")
    fun user_hes_browsed_current_headlines() {
        // Init the expected ui headlines,based on server source
        val expectedUiHeadlines = mutableListOf<UiHeadline>()

        expectedUiHeadlines.addAll(parseServerResponseToExpectedUiHeadlines(
            getJsonFromResource("json/news_api_headlines_page_1.json")))
        expectedUiHeadlines.addAll(parseServerResponseToExpectedUiHeadlines(
            getJsonFromResource("json/news_api_headlines_page_2.json")))

        // Scroll to end of listing
        scrollToBottom(2)

        // Verify all headlines shown
        onView(withId(R.id.headlines))
            .check(matches(isRecyclerViewItemsCount(expectedUiHeadlines.size)))

        expectedUiHeadlines.forEachIndexed { index, uiHeadline ->
            onView(withId(R.id.headlines))
                .perform(
                    scrollToPosition<HeadlinesAdapter.HeadlineViewHolder>(
                        index
                    )
                )
            Shadows.shadowOf(Looper.getMainLooper()).idle()
            onView(withRecyclerView(R.id.headlines).atPositionOnView(index, R.id.headlineTitle))
                .check(matches(withText(uiHeadline.title)))
            onView(withRecyclerView(R.id.headlines).atPositionOnView(index, R.id.headlineDate))
                .check(matches(withText(uiHeadline.date)))
            onView(withRecyclerView(R.id.headlines).atPositionOnView(index, R.id.headlineSource))
                .check(matches(withText(uiHeadline.sourceName)))
        }
    }

    @When("^new headlines are available$")
    fun new_headlines_are_available() {
        val firstHeadlinesPageResponse = gson.fromJson(
            getJsonFromResource("json/news_api_refreshed_headlines.json")
            , NewsApiResponse::class.java)
        val lastHeadlinesPageResponse = gson.fromJson(
            getJsonFromResource("json/news_api_headlines_last_page.json"),
            NewsApiResponse::class.java)

        clearMocks(newsApi)

        coEvery { newsApi.getHeadlines(BuildConfig.NEWS_API_KEY, NEWS_API_SOURCES,1) } returns firstHeadlinesPageResponse
        coEvery { newsApi.getHeadlines(BuildConfig.NEWS_API_KEY, NEWS_API_SOURCES,2) } returns lastHeadlinesPageResponse
    }

    @And("^user select to refresh content$")
    fun user_select_to_refresh_content() {
        onView(withId(R.id.swipe_refresh))
            .perform(swipeToRefresh())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^app should show refreshed headlines content$")
    fun app_should_show_refreshed_headlines_content() {
        // Init the expected ui headlines,based on server source
        val expectedUiHeadlines = parseServerResponseToExpectedUiHeadlines(
            getJsonFromResource("json/news_api_refreshed_headlines.json"))

        // Verify all headlines shown
        onView(withId(R.id.headlines))
            .check(matches(isRecyclerViewItemsCount(expectedUiHeadlines.size)))

        expectedUiHeadlines.forEachIndexed { index, uiHeadline ->
            onView(withId(R.id.headlines))
                .perform(
                    scrollToPosition<HeadlinesAdapter.HeadlineViewHolder>(
                        index
                    )
                )
            Shadows.shadowOf(Looper.getMainLooper()).idle()
            onView(withRecyclerView(R.id.headlines).atPositionOnView(index, R.id.headlineTitle))
                .check(matches(withText(uiHeadline.title)))
            onView(withRecyclerView(R.id.headlines).atPositionOnView(index, R.id.headlineDate))
                .check(matches(withText(uiHeadline.date)))
            onView(withRecyclerView(R.id.headlines).atPositionOnView(index, R.id.headlineSource))
                .check(matches(withText(uiHeadline.sourceName)))
        }
    }

    private fun scrollToBottom(count: Int) {
        for (i in 0 until count) {
            scenario.onActivity { activity ->
                val recycler = activity.findViewById<RecyclerView>(R.id.headlines)
                val lastPosition = recycler.adapter!!.itemCount
                recycler.smoothScrollToPosition(lastPosition)
            }

            Shadows.shadowOf(Looper.getMainLooper()).idle()
        }
    }

    private fun getJsonFromResource(resource: String): String {
        val topLevelClass = object : Any() {}.javaClass.enclosingClass!!
        val jsonResource = topLevelClass.classLoader!! // javaClass.classLoader
            .getResource(resource)

        return File(jsonResource.toURI()).readText()
    }

    private fun parseServerResponseToExpectedUiHeadlines(serverJson: String): List<UiHeadline> {
        val gson = Gson()
        val apiResponse = gson.fromJson(serverJson, NewsApiResponse::class.java)
        val dateFormat = "d MMM, HH:mm"
        val headlines = mutableListOf<UiHeadline>()

        apiResponse.articles.map { apiArticle ->
            headlines.add(
                UiHeadline(
                    apiArticle.url,
                    apiArticle.title,
                    DateTime(apiArticle.publishedAt).toString(dateFormat),
                    apiArticle.urlToImage ?: "",
                    apiArticle.source.name
                )
            )
        }

        return headlines
    }
}