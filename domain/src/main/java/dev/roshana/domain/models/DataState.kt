package dev.roshana.domain.models

/** PariSa;
coding and smoking ;)
 **/

open class DataState<T>(
    var isLoading: Boolean = false,
    val data: T? = null,
    val errorMessage: String = ""
)