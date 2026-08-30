package dev.lumora.composearch.core.network

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

/**
 * A uniform business envelope, for the very common backend that wraps every payload
 * in `{ code, success, message, result }`. A feature then models only its own `result`
 * type and returns `Resource<ApiResponse<TheResult>>`.
 *
 * **Optional.** An API that returns bare payloads (like the NewsAPI example this
 * template ships with) skips the envelope entirely and puts its DTO straight in
 * `Resource<…>`. Delete this file if your backend has no envelope; keep it if it does.
 *
 * [result] is the endpoint-specific body; it is absent on errors or no-content
 * responses. Some backends (Laravel is the usual culprit) serialise an *empty* object
 * result as `[]` rather than `{}`, which makes Gson throw; [EmptyResultAdapterFactory]
 * reads that `[]` as null while leaving genuine list results intact.
 */
data class ApiResponse<T>(
    @JsonAdapter(EmptyResultAdapterFactory::class)
    @SerializedName("result") val result: T? = null,
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("message") val message: String = "",
    @SerializedName("code") val code: Int = 0,
)
