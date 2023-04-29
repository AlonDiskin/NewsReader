package com.diskin.alon.newsreader.news.application.model

data class Headline(val id: String,
                    val published: Long,
                    val title: String,
                    val sourceName: String,
                    val sourceUrl: String,
                    val imageUrl: String,
                    val isBookmarked: Boolean)