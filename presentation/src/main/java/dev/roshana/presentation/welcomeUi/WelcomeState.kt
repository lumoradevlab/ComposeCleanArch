package dev.roshana.presentation.welcomeUi

import dev.roshana.domain.models.DataState
import kotlinx.coroutines.flow.Flow

class WelcomeState(
    var loading: Boolean? = true,
    var dataList: Flow<Boolean>? = null,
    var errorMsg: String? = ""
) : DataState<Flow<Boolean>>(
    isLoading = loading == false,
    data = dataList,
    errorMessage = errorMsg!!
)
