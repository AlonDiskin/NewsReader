package com.diskin.alon.newsreader.journey

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.newsreader.common.uitesting.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.newsreader.news.data.remote.NewsApiResponse
import com.diskin.alon.newsreader.util.DeviceUtil
import com.diskin.alon.newsreader.util.FileUtil
import com.google.common.truth.Truth.*
import com.google.gson.Gson
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When

class ShareHeadlineSteps : GreenCoffeeSteps() {

    @Given("^user launched app from device home$")
    fun user_launched_app_from_device_home() {
        DeviceUtil.launchApp()
    }

    @When("^user open news headlines screen$")
    fun user_open_news_headlines_screen() {
        onView(withContentDescription("Latest"))
            .perform(click())
    }

    @And("^share first listed headline$")
    fun share_first_listed_headline() {
        Intents.init()
        onView(withRecyclerView(com.diskin.alon.newsreader.news.presentation.R.id.headlines)
            .atPositionOnView(0,com.diskin.alon.newsreader.news.presentation.R.id.shareImageButton))
            .perform(click())
    }

    @Then("^app should display device sharing menu$")
    fun app_should_display_device_sharing_menu() {
        val gson = Gson()
        val json = FileUtil.readStringFromFile("assets/json/news_api_headlines_page.json")
        val apiResponse = gson.fromJson(json, NewsApiResponse::class.java)
        val expectedArticleUrl = apiResponse.articles[0].url

        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_CHOOSER))
        Intents.intended(IntentMatchers.hasExtraWithKey(Intent.EXTRA_INTENT))

        val intent = Intents.getIntents().first().extras?.get(Intent.EXTRA_INTENT) as Intent

        assertThat(intent.type).isEqualTo("text/plain")
        assertThat(intent.getStringExtra(Intent.EXTRA_TEXT))
            .isEqualTo(expectedArticleUrl)

        Intents.release()
    }
}