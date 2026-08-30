/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.query

import dev.lumora.composearch.core.common.result.AppError

/**
 * UI-facing state of a server-state read. The query engine ([QueryEntry]) emits
 * this; screens render it (via QueryContent in :core:ui) and hooks ([useQuery])
 * surface it inside a [QueryResult].
 *
 * It is a discriminated union on purpose — there is exactly one Loading, one
 * Success, one Failure, so a screen can never forget a case. Stale-while-
 * revalidate is modelled by [Success.refreshing]: the data stays on screen while
 * a background refetch runs, instead of flashing the spinner.
 */
sealed interface QueryState<out T> {
    data object Loading : QueryState<Nothing>
    data class Success<out T>(val data: T, val refreshing: Boolean = false) : QueryState<T>
    data class Failure(val error: AppError) : QueryState<Nothing>
}

val <T> QueryState<T>.dataOrNull: T?
    get() = (this as? QueryState.Success)?.data

val QueryState<*>.errorOrNull: AppError?
    get() = (this as? QueryState.Failure)?.error
