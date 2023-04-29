package com.diskin.alon.newsreader.util

import com.diskin.alon.newsreader.news.data.BuildConfig
import com.diskin.alon.newsreader.news.data.remote.NEWS_API_PATH_HEADLINES
import com.diskin.alon.newsreader.news.data.remote.NEWS_API_QUERY_PARAM_API_KEY
import com.diskin.alon.newsreader.news.data.remote.NEWS_API_QUERY_PARAM_SOURCES
import com.diskin.alon.newsreader.news.data.remote.NEWS_API_SOURCES
import okhttp3.HttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

object NetworkUtil {

    private var _server: MockWebServer? = null
    val dispatcher = TestDispatcher()
    val server: MockWebServer
        get() {
            return _server ?:
            throw NullPointerException("MockWebServer has not been initialized yet")
        }

    private var _url: HttpUrl? = null
    val url: HttpUrl
        get() {
            return _url ?:
            throw NullPointerException("MockWebServer has not been initialized yet")
        }

    fun initServer() {
        val mockWebServer = MockWebServer()
        _server = mockWebServer
        _url = mockWebServer.url("/")

        server.setDispatcher(dispatcher)
    }

    open class TestDispatcher : Dispatcher() {

        override fun dispatch(request: RecordedRequest): MockResponse {
            val expectedPath = "/".plus(NEWS_API_PATH_HEADLINES)
            return when(request.requestUrl.uri().path) {
                expectedPath -> {
                    return if (
                        request.requestUrl.queryParameter(NEWS_API_QUERY_PARAM_API_KEY) == BuildConfig.NEWS_API_KEY &&
                        request.requestUrl.queryParameter(NEWS_API_QUERY_PARAM_SOURCES) == NEWS_API_SOURCES
                    ) {
                        MockResponse()
                            .setBody(FileUtil.readStringFromFile("assets/json/news_api_headlines_page.json"))
                            .setResponseCode(200)
                    } else {
                        MockResponse().setResponseCode(404)
                    }
                }

                else -> MockResponse().setResponseCode(404)
            }
        }
    }
}