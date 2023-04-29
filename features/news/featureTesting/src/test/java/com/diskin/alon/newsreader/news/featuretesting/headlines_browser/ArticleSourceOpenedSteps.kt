package com.diskin.alon.newsreader.news.featuretesting.headlines_browser

import android.content.Intent
import android.net.Uri
import android.os.Looper
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import com.diskin.alon.newsreader.common.uitesting.HiltTestActivity
import com.diskin.alon.newsreader.common.uitesting.RecyclerViewMatcher.*
import com.diskin.alon.newsreader.common.uitesting.launchFragmentInHiltContainer
import com.diskin.alon.newsreader.news.data.BuildConfig
import com.diskin.alon.newsreader.news.data.remote.NEWS_API_SOURCES
import com.diskin.alon.newsreader.news.data.remote.NewsApi
import com.diskin.alon.newsreader.news.data.remote.NewsApiResponse
import com.diskin.alon.newsreader.news.presentation.R
import com.diskin.alon.newsreader.news.presentation.model.UiHeadline
import com.diskin.alon.newsreader.news.presentation.ui.HeadlinesFragment
import com.google.gson.Gson
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import io.mockk.coEvery
import org.joda.time.DateTime
import org.robolectric.Shadows
import java.io.File

class ArticleSourceOpenedSteps(
    private val newsApi: NewsApi
) : GreenCoffeeSteps() {

    private val scenario: ActivityScenario<HiltTestActivity>
    private val gson = Gson()

    init {
        // Stub remote api mock
        val firstHeadlinesPageResponse = gson.fromJson(getJsonFromResource("json/news_api_headlines_page_1.json"),
            NewsApiResponse::class.java)

        coEvery { newsApi.getHeadlines(BuildConfig.NEWS_API_KEY, NEWS_API_SOURCES,any()) } returns firstHeadlinesPageResponse

        // Launch headlines fragment
        scenario = launchFragmentInHiltContainer<HeadlinesFragment>()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Given("^user selected to read first shown headlines in browser$")
    fun user_selected_to_read_first_shown_headlines_in_browser() {
        Intents.init()

        onView(withRecyclerView(R.id.headlines)
            .atPositionOnView(0, R.id.card))
            .perform(ViewActions.click())

        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^app should open article source in device default browser$")
    fun app_should_open_article_source_in_device_default_browser() {
        val json = getJsonFromResource("json/news_api_headlines_page_1.json")
        val expectedUiHeadlines = parseServerResponseToExpectedUiHeadlines(json)

        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_VIEW))
        Intents.intended(IntentMatchers.hasData(Uri.parse(expectedUiHeadlines
            .first().sourceUrl)))

        Intents.release()
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
                    apiArticle.source.name,
                    apiArticle.url
                )
            )
        }

        return headlines
    }
}