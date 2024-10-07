package com.example.news_compose_c40.api

import com.example.news_compose_c40.model.article.ArticlesResponse
import com.example.news_compose_c40.model.source.SourcesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService {

    @GET("top-headlines/sources")
    suspend fun getNewsSources(
        @Query("category") categoryId: String,
    ): SourcesResponse


    @GET("everything")
    suspend fun getNewsBySource(
        @Query("sources") sourceId: String
    ): ArticlesResponse

    @GET("everything")
    suspend fun getArticle(
        @Query("q") title: String,
        @Query("searchIn") searchIn:String ="title"
    ): ArticlesResponse

    @GET("everything")
    suspend fun getArticles(
        @Query("q") searchQuery: String): ArticlesResponse

}
