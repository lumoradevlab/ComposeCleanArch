package dev.lumora.composearch.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.lumora.composearch.core.designsystem.components.AppErrorState
import dev.lumora.composearch.core.designsystem.components.AppLoading
import dev.lumora.composearch.core.query.QueryResult
import dev.lumora.composearch.core.query.QueryState

/**
 * Renders a [QueryState]: spinner while loading, error+retry on failure, and your
 * content on success. This is what keeps screens tiny — they describe the success UI
 * only, and never hand-roll loading/error handling.
 *
 * ```
 * val articles = useHeadlines()
 * QueryContent(articles) { list -> ArticleList(list) }
 * ```
 */
@Composable
fun <T> QueryContent(
    state: QueryState<T>,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {},
    content: @Composable (T) -> Unit,
) {
    when (state) {
        is QueryState.Loading -> AppLoading(modifier)
        is QueryState.Failure -> AppErrorState(
            message = state.error.message,
            onRetry = onRetry,
            modifier = modifier,
        )
        is QueryState.Success -> content(state.data)
    }
}

/**
 * Convenience overload for the [QueryResult] returned by `useQuery` — renders its
 * [QueryResult.state] and defaults retry to [QueryResult.refetch].
 */
@Composable
fun <T> QueryContent(
    result: QueryResult<T>,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = result.refetch,
    content: @Composable (T) -> Unit,
) {
    QueryContent(result.state, modifier, onRetry, content)
}
