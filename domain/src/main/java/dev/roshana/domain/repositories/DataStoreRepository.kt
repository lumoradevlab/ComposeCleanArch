package dev.roshana.domain.repositories

import kotlinx.coroutines.flow.Flow

/** PariSa;
coding and smoking ;)
 **/

interface DataStoreRepository {

    suspend fun saveOnOnBoardingState(completed: Boolean)
    suspend fun readOnBoardingState(): Flow<Boolean>

}
