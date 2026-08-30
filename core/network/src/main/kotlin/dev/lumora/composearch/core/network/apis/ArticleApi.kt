package dev.lumora.composearch.core.network.apis

import dev.lumora.composearch.core.network.Resource
import dev.lumora.composearch.core.network.data.article.ArticleListResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Top headlines from NewsAPI — the example endpoint this template is built around.
 *
 * **One Api interface per area.** Do not grow this into a mega-`Api` holding every
 * endpoint in the app: each area gets its own interface in this package, so a
 * 200-endpoint god-interface never happens. Every method returns [Resource]<T>, which
 * the `ResourceCallAdapterFactory` produces — so there is no per-call `safeApiCall`.
 *
 * NewsAPI returns a bare `{ status, totalResults, articles }` body with no business
 * envelope, so the result type is the DTO directly. A backend that *does* wrap its
 * payloads would return `Resource<ApiResponse<ArticleListResponseDto>>` instead.
 */
interface ArticleApi {
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("sources") sources: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): Resource<ArticleListResponseDto>
}

/**
 * Stable query-cache keys owned by the article area.
 *
 * Keys live next to the Api interface so the hook that *reads* and the mutation that
 * *invalidates* share one definition — a stringly-typed key duplicated at two call
 * sites is how stale screens happen. Parameterised reads get a function (see
 * [headlines]) so each argument gets its own cache slot.
 */
object ArticleKeys {
    /** Keyed per source + page — each combination is its own cache slot. */
    fun headlines(source: String, page: Int): String = "articles.headlines.$source.$page"
}
