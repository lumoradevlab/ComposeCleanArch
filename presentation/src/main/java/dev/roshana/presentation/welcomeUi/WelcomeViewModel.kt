package dev.roshana.presentation.welcomeUi

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.roshana.domain.usecases.DataStoreUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val dataStoreUseCase: DataStoreUseCase
) : ViewModel() {

    private val _onBoardingState: MutableState<Boolean> = mutableStateOf(false)
    val onBoardingState: State<Boolean>
        get() = _onBoardingState

    fun saveOnBoardingState(completed: Boolean) {
        viewModelScope.launch {
            dataStoreUseCase.invoke(completed)
        }
    }

    /* private fun readOnBoardingState(): Flow<Boolean> {

     }*/
}