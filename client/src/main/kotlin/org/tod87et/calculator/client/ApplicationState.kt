package org.tod87et.calculator.client

import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.tod87et.calculator.client.api.ApiResult
import org.tod87et.calculator.client.api.AppApi
import org.tod87et.calculator.client.api.LocalAppApi

sealed interface ApplicationState {
    val snackbarHostState: SnackbarHostState

    @Stable
    class ConnectionInitializationScreen(
        override val snackbarHostState: SnackbarHostState,
        initialHostString: String = "",
    ) : ApplicationState {
        private val hostStringState = mutableStateOf(initialHostString)
        var hostString by hostStringState

        fun flowToMainScreen(
            coroutineScope: CoroutineScope,
            updateAppState: (ApplicationState) -> Unit,
        ) {
            val host = hostString

            val api = if (host == "local") {
                LocalAppApi()
            } else {
                coroutineScope.launch {
                    showSnackbar("Cannot create api")
                }
                return
            }

            updateAppState(MainScreen(snackbarHostState, api))
        }
    }

    @Stable
    class MainScreen(
        override val snackbarHostState: SnackbarHostState,
        val appApi: AppApi,
        initialExpression: String = "",
    ) : ApplicationState {
        private val expressionState = mutableStateOf(initialExpression)
        var expression by expressionState

        private val submittedExpressionState: MutableState<String?> = mutableStateOf(null)
        var submittedExpression by submittedExpressionState

        private val lastResultState: MutableState<ApiResult<ComputationResult>?> = mutableStateOf(null)
        var lastResult by lastResultState

        val isWaitingForResponse: Boolean by derivedStateOf {
            submittedExpression != null
        }

        fun requestCalculation(coroutineScope: CoroutineScope) {
            if (isWaitingForResponse) {
                return
            }
            submittedExpression = expression
            lastResult = null

            coroutineScope.launch {
                lastResult = appApi.calculatorApi.compute(expression)
                submittedExpression = null
            }
        }
    }
}

suspend fun ApplicationState.showSnackbar(
    message: String,
    actionLabel: String? = null,
    duration: SnackbarDuration = SnackbarDuration.Short,
): SnackbarResult = snackbarHostState.showSnackbar(message, actionLabel, duration)
