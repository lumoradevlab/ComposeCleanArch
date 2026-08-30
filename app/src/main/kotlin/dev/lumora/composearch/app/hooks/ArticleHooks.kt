/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.app.hooks

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import dev.lumora.composearch.core.model.Article
import dev.lumora.composearch.core.network.apis.ArticleKeys
import dev.lumora.composearch.core.network.repositories.ArticleRepository
import dev.lumora.composearch.core.query.QueryOptions
import dev.lumora.composearch.core.query.QueryResult
import dev.lumora.composearch.core.query.useQuery

/**
 * Hooks for the article area — one file per area (`ArticleHooks`, `ProfileHooks`, …).
 *
 * A hook is the whole "view model" for a read-only screen: it names the cache key,
 * points at a repository through [HookProvider], and hands back a [QueryResult] the
 * screen renders with `QueryContent`. No ViewModel, no state class, no `init { load() }`.
 *
 * Add a state holder only when a screen has real *local* state — a form, a multi-step
 * flow, something that must survive configuration changes. It then calls these same
 * hooks rather than becoming a second way to fetch data.
 */
@Composable
fun useHeadlines(
    source: String = ArticleRepository.DEFAULT_SOURCE,
    page: Int = 1,
    options: QueryOptions = QueryOptions(),
): QueryResult<List<Article>> {
    val provider = hiltViewModel<HookProvider>()

    return useQuery(
        key = ArticleKeys.headlines(source, page),
        fetcher = { provider.articleRepository.getHeadlines(source, page) },
        options = options,
    )
}
