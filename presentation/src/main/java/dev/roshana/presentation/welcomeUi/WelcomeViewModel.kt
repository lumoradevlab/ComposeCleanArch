package dev.roshana.presentation.welcomeUi

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.roshana.domain.usecases.DataStoreUseCase
import dev.roshana.presentation.navigation.Screens
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val dataStoreUseCase: DataStoreUseCase
) : ViewModel() {

    private val _onBoardingState: MutableState<Boolean> = mutableStateOf(false)
    val onBoardingState: State<Boolean>
        get() = _onBoardingState

    private val _welcomeState: MutableState<WelcomeState> = mutableStateOf(WelcomeState())
    val welcomeState: State<WelcomeState>
        get() = _welcomeState

    private val _startDestination: MutableState<String> =
        mutableStateOf(Screens.WelcomeScreen.route)
    val startDestination: State<String>
        get() = _startDestination


    private val handler = CoroutineExceptionHandler { _, exception ->
        _welcomeState.value.loading = false
        _welcomeState.value = WelcomeState(
            errorMsg = exception.message!!
        )
    }


    init {
        readOnBoardingState()
    }


    fun saveOnBoardingState(completed: Boolean) {
        viewModelScope.launch {
            dataStoreUseCase.invoke(completed)
        }
    }

    private fun readOnBoardingState() {
        viewModelScope.launch(handler) {
            dataStoreUseCase.invoke().collect { completed ->
                if (completed) {
                    _startDestination.value = Screens.ArticleListScreen.route
                } else {
                    _startDestination.value = Screens.WelcomeScreen.route
                }
            }
            _welcomeState.value.loading = false
        }
    }
}