package dev.roshana.domain.models

open class DataState<T>(
    var isLoading: Boolean = false,
    val data: T? = null,
    val errorMessage: String = ""
)