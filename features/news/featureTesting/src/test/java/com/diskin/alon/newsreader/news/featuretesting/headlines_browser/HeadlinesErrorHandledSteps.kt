package com.diskin.alon.newsreader.news.featuretesting.headlines_browser

import android.content.Context
import android.os.Looper
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.diskin.alon.newsreader.common.uitesting.HiltTestActivity
import com.diskin.alon.newsreader.common.uitesting.launchFragmentInHiltContainer
import com.diskin.alon.newsreader.news.data.BuildConfig
import com.diskin.alon.newsreader.news.data.remote.NEWS_API_SOURCES
import com.diskin.alon.newsreader.news.data.remote.NewsApi
import com.diskin.alon.newsreader.news.presentation.ui.HeadlinesFragment
import com.google.common.truth.Truth.*
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowToast
import retrofit2.HttpException
import java.io.IOException

class HeadlinesErrorHandledSteps(
    private val apiMock: NewsApi
) : GreenCoffeeSteps() {

    private lateinit var scenario: ActivityScenario<HiltTestActivity>

    @Given("^an existing error of type \"([^\"]*)\"$")
    fun an_existing_error_of_type_something(error: String) {
        when(error) {
            "device connectivity" -> coEvery {
                apiMock.getHeadlines(BuildConfig.NEWS_API_KEY, NEWS_API_SOURCES, any())
            } throws IOException()

            "remote server" -> coEvery {
                apiMock.getHeadlines(BuildConfig.NEWS_API_KEY, NEWS_API_SOURCES, any())
            } throws mockk<HttpException>()

            "app internal error" -> {
                val throwable = mockk<Throwable>()

                every { throwable.printStackTrace() } returns Unit
                coEvery {
                    apiMock.getHeadlines(BuildConfig.NEWS_API_KEY, NEWS_API_SOURCES, any())
                } throws throwable
            }

            else -> throw IllegalArgumentException("Unknown scenario argument:$error")
        }
    }

    @When("^user open headlines screen$")
    fun user_open_headlines_screen() {
        // Launch headlines fragment
        scenario = launchFragmentInHiltContainer<HeadlinesFragment>()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^app should handle error as \"([^\"]*)\"$")
    fun app_should_handle_error_as_something(handling: String) {
        val actualToastMessage = ShadowToast.getTextOfLatestToast()
        val context = ApplicationProvider.getApplicationContext<Context>()
        val expectedMessage = when(handling) {
            "show device connection error message" -> context.getString(
               com.diskin.alon.newsreader.news.presentation.R.string.error_message_device_connection)

            "show remote server error message" -> context.getString(
                com.diskin.alon.newsreader.news.presentation.R.string.error_message_remote_server)

            "show app error message" -> context.getString(
                com.diskin.alon.newsreader.news.presentation.R.string.error_message_internal)

            else -> throw IllegalArgumentException("Unknown scenario argument:$handling")
        }

        assertThat(actualToastMessage).isEqualTo(expectedMessage)
    }
}