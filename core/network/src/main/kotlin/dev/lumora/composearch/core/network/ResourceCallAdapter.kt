/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.network

import dev.lumora.composearch.core.common.result.HttpStatusException
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Makes every Retrofit endpoint return [Resource]<T> directly — this is the
 * "no safeApiCall" piece. Declare the call site as the data you want:
 *
 * ```
 * interface AssetApi {
 *     @GET("assets") suspend fun assets(): Resource<List<AssetDto>>
 * }
 * ```
 *
 * The adapter centralises "parse whatever came back": a 2xx body becomes
 * [Resource.data]; a non-2xx body is run through [ResponseErrorParser] into a
 * [ResponseError]; a transport failure (offline/timeout) becomes a network error.
 * It never lets an exception escape, so a dynamic or unexpected server payload can
 * never crash the call — the worst case is a [Resource] carrying a best-effort
 * error with the raw body attached.
 *
 * Per-endpoint *success* shapes are handled by the DTO you put in `Resource<…>`
 * (use kotlinx's `JsonContentPolymorphicSerializer` when one endpoint can return
 * more than one shape); this adapter is agnostic to what T is.
 */
class ResourceCallAdapterFactory(
    private val parser: ResponseErrorParser,
) : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        // Suspend functions reach here as Call<Resource<T>>.
        if (getRawType(returnType) != Call::class.java || returnType !is ParameterizedType) return null
        val responseType = getParameterUpperBound(0, returnType)
        if (getRawType(responseType) != Resource::class.java || responseType !is ParameterizedType) return null
        val dataType = getParameterUpperBound(0, responseType)
        return ResourceCallAdapter<Any?>(dataType, parser)
    }
}

private class ResourceCallAdapter<T>(
    private val responseType: Type,
    private val parser: ResponseErrorParser,
) : CallAdapter<T, Call<Resource<T>>> {
    override fun responseType(): Type = responseType
    override fun adapt(call: Call<T>): Call<Resource<T>> = ResourceCall(call, parser)
}

/** Wraps the real [delegate] call so success/error/transport-failure all map to a [Resource]. */
private class ResourceCall<T>(
    private val delegate: Call<T>,
    private val parser: ResponseErrorParser,
) : Call<Resource<T>> {

    override fun enqueue(callback: Callback<Resource<T>>) {
        delegate.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                callback.onResponse(this@ResourceCall, Response.success(response.toResource()))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onResponse(this@ResourceCall, Response.success(t.toResource()))
            }
        })
    }

    override fun execute(): Response<Resource<T>> =
        Response.success(runCatching { delegate.execute().toResource() }.getOrElse { it.toResource() })

    private fun Response<T>.toResource(): Resource<T> =
        if (isSuccessful) {
            Resource(data = body())
        } else {
            val raw = runCatching { errorBody()?.string() }.getOrNull()
            Resource(error = parser.parse(code(), raw))
        }

    private fun Throwable.toResource(): Resource<T> {
        val error = when (this) {
            is IOException -> ResponseError(
                message = "No internet connection",
                code = HttpStatusException.NETWORK_ERROR_CODE,
            )
            else -> ResponseError(
                message = message ?: "Unexpected error",
                code = HttpStatusException.UNKNOWN_ERROR_CODE,
            )
        }
        return Resource(error = error)
    }

    override fun clone(): Call<Resource<T>> = ResourceCall(delegate.clone(), parser)
    override fun isExecuted(): Boolean = delegate.isExecuted
    override fun cancel() = delegate.cancel()
    override fun isCanceled(): Boolean = delegate.isCanceled
    override fun request(): Request = delegate.request()
    override fun timeout(): Timeout = delegate.timeout()
}
