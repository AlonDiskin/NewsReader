package com.diskin.alon.newsreader.news.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET(NEWS_API_PATH_HEADLINES)
    suspend fun getHeadlines(
        @Query(NEWS_API_QUERY_PARAM_API_KEY) apiKey: String,
        @Query(NEWS_API_QUERY_PARAM_SOURCES) sources: String,
        @Query(NEWS_API_QUERY_PARAM_PAGE) page: Int
    ): NewsApiResponse
}