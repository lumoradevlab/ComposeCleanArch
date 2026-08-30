package dev.lumora.composearch.core.common.result

/**
 * The ONE result type at the domain/UI boundary. A repository either returns a
 * domain model or throws; anything that wants to hand a failure back as a *value*
 * (rather than a thrown exception) uses this — so no layer invents its own
 * `Either`/`Outcome`/sealed `Result` clone.
 *
 * Note the query layer does not need this: `useQuery`/`useMutation` catch the
 * fetcher's throw and surface [AppError] inside a `QueryState`. Reach for
 * [AppResult] in the places outside a hook — a use case, a validator, a state
 * holder's submit function.
 */
sealed interface AppResult<out T> {
    data class Success<out T>(val data: T) : AppResult<T>
    data class Failure(val error: AppError) : AppResult<Nothing>

    companion object {
        /** Runs [block], returning its value as [Success] or its throw mapped to [Failure]. */
        inline fun <T> catching(block: () -> T): AppResult<T> =
            try {
                Success(block())
            } catch (throwable: Throwable) {
                Failure(AppError.from(throwable))
            }
    }
}

val <T> AppResult<T>.dataOrNull: T?
    get() = (this as? AppResult.Success)?.data

val AppResult<*>.errorOrNull: AppError?
    get() = (this as? AppResult.Failure)?.error

inline fun <T, R> AppResult<T>.map(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(data))
    is AppResult.Failure -> this
}

inline fun <T> AppResult<T>.onSuccess(action: (T) -> Unit): AppResult<T> =
    also { if (this is AppResult.Success) action(data) }

inline fun <T> AppResult<T>.onFailure(action: (AppError) -> Unit): AppResult<T> =
    also { if (this is AppResult.Failure) action(error) }
