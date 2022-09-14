package dev.roshana.domain.repositories

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {

    suspend fun saveOnOnBoardingState(completed: Boolean)
    suspend fun readOnBoardingState(): Flow<Boolean>

}
