package dev.roshana.domain.usecases

import dev.roshana.domain.repositories.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** PariSa;
coding and smoking ;)
 **/

class DataStoreUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke(completed: Boolean) {
        return dataStoreRepository.saveOnOnBoardingState(completed)
    }

    suspend operator fun invoke(): Flow<Boolean> {
        return dataStoreRepository.readOnBoardingState()
    }
}