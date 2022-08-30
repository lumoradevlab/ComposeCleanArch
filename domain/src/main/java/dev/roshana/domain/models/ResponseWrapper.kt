package dev.roshana.domain.models

import com.google.gson.annotations.SerializedName

data class ResponseWrapper<T : Any> (
    @SerializedName("status") val status: String,
    @SerializedName("total_results") val totalResults: Int,
    @SerializedName("articles") val articles: T
)