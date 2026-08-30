package dev.lumora.composearch.core.network

import dev.lumora.composearch.core.common.result.HttpStatusException

/**
 * The uniform result of a server call. Every endpoint returns this (see
 * [ResourceCallAdapterFactory]), so repositories and the query client never touch
 * a raw Retrofit `Response` or a thrown `HttpException`.
 *
 * Whatever the server sends collapses into exactly one of two outcomes: [data]
 * present (success) or [error] present (failure). A typed body, an empty body, an
 * error in any shape, malformed JSON, or a dropped connection all land here
 * without throwing. The untouched payload is preserved in [ResponseError.raw] so
 * nothing the server returned is ever lost — even fields we didn't model.
 */
data class Resource<T>(
    val data: T? = null,
    val isLoading: Boolean = false,
    val error: ResponseError? = null,
) {
    val isSuccess: Boolean get() = error == null
    val isError: Boolean get() = error != null

    /** Unwrap for the query client's fetcher, throwing a typed exception on failure. */
    fun dataOrThrow(): T =
        data ?: throw ResponseException(error ?: ResponseError("Empty response body", code = -1))
}

/**
 * A normalised failure. [message] is the best-effort human text pulled out of
 * whatever shape arrived; [fieldErrors] holds validation maps when present;
 * [code] is the HTTP/business code; [raw] is the original body so you can dig out
 * anything not modelled here.
 */
data class ResponseError(
    val message: String,
    val code: Int,
    val fieldErrors: Map<String, List<String>> = emptyMap(),
    val raw: String? = null,
)

/**
 * Thrown by [Resource.dataOrThrow] so a failure carries the parsed [error] into the
 * query layer. Implements [HttpStatusException] so `AppError.from` can map it by
 * status code (e.g. 429 → `AppError.RateLimited`) without :core:common knowing this type.
 */
class ResponseException(val error: ResponseError) : Exception(error.message), HttpStatusException {
    override val httpStatusCode: Int get() = error.code
}
