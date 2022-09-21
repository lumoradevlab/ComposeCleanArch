package dev.roshana.data.repository.base

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** PariSa;
coding and smoking ;)
 **/

open class BaseRepository {

    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                Result.success(apiCall.invoke())
            } catch (throwable: Throwable) {
                Result.failure(throwable)
            }
        }
    }
}
