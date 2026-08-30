/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.network.repositories

import dev.lumora.composearch.core.model.Article
import dev.lumora.composearch.core.network.apis.ArticleApi
import dev.lumora.composearch.core.network.data.article.toModel
import javax.inject.Inject

/**
 * The articles data source.
 *
 * A repository's whole job: call the Api, unwrap the transport [Resource], map DTOs to
 * domain models, and return them — throwing on failure. It never returns a `Resource`
 * or a raw `Result`: `dataOrThrow()` converts a failure into a typed exception, and the
 * query layer maps that to an `AppError`. That is what keeps ONE result type at the
 * domain/UI boundary.
 *
 * Note there is no interface here. A repository with exactly one implementation and no
 * test double doesn't need one; extract an interface when a second implementation or a
 * fake genuinely appears, not preemptively.
 */
class ArticleRepository @Inject constructor(
    private val api: ArticleApi,
) {
    suspend fun getHeadlines(
        source: String = DEFAULT_SOURCE,
        page: Int = 1,
        pageSize: Int = DEFAULT_PAGE_SIZE,
    ): List<Article> =
        api.getTopHeadlines(source, page, pageSize)
            .dataOrThrow()
            .toModel()

    companion object {
        const val DEFAULT_SOURCE = "techcrunch"
        const val DEFAULT_PAGE_SIZE = 20
    }
}
