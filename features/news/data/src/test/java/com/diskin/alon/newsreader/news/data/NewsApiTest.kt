package com.diskin.alon.newsreader.news.data

import com.diskin.alon.newsreader.news.data.remote.*
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class NewsApiTest {

    // Test subject
    private lateinit var api: NewsApi

    private val server = MockWebServer()
    private val gson = Gson()

    @Before
    fun setUp() {
        // Start mock web server
        server.start()

        // Create test subject
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BASIC
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
            .create(NewsApi::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun requestApiHeadlines_MapServerResponse() = runTest {
        // Given
        val apiResponsePath = "news_api_headlines.json"
        val serverJson = getJsonFromResource(apiResponsePath)
        val expectedApiResponse = gson.fromJson(serverJson, NewsApiResponse::class.java)
        val expectedServerHeadlinesPath = "/".plus(NEWS_API_PATH_HEADLINES)
        val requestPage = 1
        val dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when(request.requestUrl.uri().path) {
                    expectedServerHeadlinesPath -> {
                        return if (
                            request.requestUrl.queryParameter(NEWS_API_QUERY_PARAM_API_KEY) == BuildConfig.NEWS_API_KEY &&
                            request.requestUrl.queryParameter(NEWS_API_QUERY_PARAM_PAGE) == requestPage.toString() &&
                            request.requestUrl.queryParameter(NEWS_API_QUERY_PARAM_SOURCES) == NEWS_API_SOURCES
                        ) {
                            MockResponse()
                                .setBody(getJsonFromResource(apiResponsePath))
                                .setResponseCode(200)
                        } else {
                            MockResponse().setResponseCode(404)
                        }
                    }

                    else -> MockResponse().setResponseCode(404)
                }
            }
        }

        server.setDispatcher(dispatcher)

        // When
        val actualApiResponse = api.getHeadlines(BuildConfig.NEWS_API_KEY, NEWS_API_SOURCES,requestPage)

        // Then
        assertThat(actualApiResponse).isEqualTo(expectedApiResponse)
    }

    private fun getJsonFromResource(resource: String): String {
        val topLevelClass = object : Any() {}.javaClass.enclosingClass!!
        val jsonResource = topLevelClass.classLoader!! // javaClass.classLoader
            .getResource(resource)

        return File(jsonResource.toURI()).readText()
    }
}