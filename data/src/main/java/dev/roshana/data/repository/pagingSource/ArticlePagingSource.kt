package dev.roshana.data.repository.pagingSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.roshana.data.network.api.ApiService
import dev.roshana.domain.models.article.Article

class ArticlePagingSource(
    private val apiService: ApiService
) : PagingSource<Int, Article>() {
    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorposition ->
            state.closestPageToPosition(anchorposition)?.prevKey?.plus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        return try {
            val page = params.key ?: 1
            val response = apiService.getTechCrunchNews(
                page = page,
                sources = "techcrunch",
                apiKey = "de196ac120164019a0911ea8191f85e4"
            )
            val items = response.articles?.map {
                Article(author = it.author, title = it.title, url = it.url)
            }
            val prevKey = if (page > 1) page - 1 else null
            val nextKey = if (items?.isNotEmpty() == true) page + 1 else null
            LoadResult.Page(
                data = items!!,
                nextKey = nextKey,
                prevKey = null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
        /* return try {
             val page = params.key ?: 1 // set page 1 as default
             val pageSize = params.loadSize
             val response = apiService.getTechCrunchNews(
                 page = page,
                 sources = "techcrunch",
                 apiKey = "de196ac120164019a0911ea8191f85e4"
             )
             val items = response.articles?.map {
                 Article(author = it.author, title = it.title, url = it.url)
             }
             val prevKey = if (page > 1) page - 1 else null
             val nextKey = if (items?.isNotEmpty() == true) page + 1 else null
             LoadResult.Page(items!!, prevKey, nextKey)
         } catch (e: Exception) {
             e.printStackTrace()
             LoadResult.Error(e)
         }*/
    }


}