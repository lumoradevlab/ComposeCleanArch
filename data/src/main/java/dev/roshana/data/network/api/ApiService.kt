package dev.roshana.data.network.api

import dev.roshana.data.network.models.dto.ArticleDto
import dev.roshana.data.network.utils.TOP_HEAD_LINES
import dev.roshana.domain.models.ResponseWrapper
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET(TOP_HEAD_LINES)
    suspend fun getTechCrunchNews(
        @Query("page") page: Int,
        @Query("sources") sources: String = "techcrunch",
        @Query("apikey") apiKey: String = "de196ac120164019a0911ea8191f85e4"
    ): ResponseWrapper<List<ArticleDto>>


}