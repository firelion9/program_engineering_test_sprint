package org.tod87et.calculator.client

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.tod87et.calculator.client.api.ApiResult
import org.tod87et.calculator.client.api.AppApi
import org.tod87et.calculator.client.api.ComputationResult
import org.tod87et.calculator.client.api.LocalAppApi
import org.tod87et.calculator.client.api.NetworkAppApi

sealed interface ApplicationState {
    val snackbarHostState: SnackbarHostState

    @Stable
    class ConnectionInitializationScreen(
        override val snackbarHostState: SnackbarHostState,
        initialHostString: String = "",
    ) : ApplicationState {
        private val hostStringState = mutableStateOf(initialHostString)
        var hostString by hostStringState

        private val isConnectingState = mutableStateOf(false)
        var isConnecting by isConnectingState

        fun flowToMainScreen(
            coroutineScope: CoroutineScope,
            updateAppState: (ApplicationState) -> Unit,
        ) {
            val host = hostString

            coroutineScope.launch {
                if (isConnecting) {
                    return@launch
                }
                isConnecting = true
                try {
                    connectAndFlowToMainScreen(host, updateAppState, coroutineScope)

                } finally {
                    isConnecting = false
                }
            }
        }

        private suspend fun connectAndFlowToMainScreen(
            host: String,
            updateAppState: (ApplicationState) -> Unit,
            coroutineScope: CoroutineScope,
        ) {
            var host1 = host
            val api = if (host1 == "local") {
                LocalAppApi()
            } else {
                if (!host1.startsWith("http")) {
                    host1 = "http://$host1"
                }
                NetworkAppApi(host1, KTorFactory.createHttpClient())
            }

            when (val history = api.historyApi.listHistory(limit = 1)) {
                is ApiResult.Success -> {
                    updateAppState(MainScreen(snackbarHostState, host, api))
                }

                is ApiResult.Failure -> {
                    coroutineScope.launch {
                        showSnackbar(history.reason)
                    }
                }
            }
        }
    }

    @Stable
    class MainScreen(
        override val snackbarHostState: SnackbarHostState,
        val hostString: String,
        val appApi: AppApi,
        initialExpression: String = "",
    ) : ApplicationState {
        private val expressionState = mutableStateOf(initialExpression)
        var expression
            get() = expressionState.value
            set(value) {
                expressionState.value = value.filter {
                    it in expressionChars
                }
            }

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

        fun onBackSpace(clear: Boolean = false) {
            expression = if (clear) {
                ""
            } else {
                expression.let {
                    it.substring(0, it.lastIndex.coerceAtLeast(0))
                }
            }
        }

        fun flowToConnectionInitializationScreen(
            updateAppState: (ApplicationState) -> Unit,
            coroutineScope: CoroutineScope,
        ) {
            updateAppState(ConnectionInitializationScreen(snackbarHostState, hostString))
        }

        fun flowToHistoryScreen(
            updateAppState: (ApplicationState) -> Unit,
            coroutineScope: CoroutineScope,
        ) {
            updateAppState(HistoryScreen(snackbarHostState, hostString, appApi, expression))
        }
    }

    @Stable
    class HistoryScreen(
        override val snackbarHostState: SnackbarHostState,
        val hostString: String,
        val appApi: AppApi,
        val calculatorExpression: String,
        val lazyListState: LazyListState = LazyListState(0),
    ) : ApplicationState {
        private val itemsCache = mutableStateListOf<ComputationResult>()
        val historyItems: List<ComputationResult> get() = itemsCache

        private val isLoadingItemsState = mutableStateOf(false)
        val isLoadingItems by isLoadingItemsState

        private val hasMoreItemsState = mutableStateOf(true)
        val hasMoreItems by hasMoreItemsState

        fun onItemComposed(coroutineScope: CoroutineScope, id: String) {
            if (itemsCache.isEmpty() || itemsCache.lastOrNull()?.id == id) {
                loadMoreItems(coroutineScope)
            }
        }

        fun loadMoreItems(coroutineScope: CoroutineScope) {
            coroutineScope.launch {
                if (!hasMoreItems || isLoadingItems) {
                    return@launch
                }

                isLoadingItemsState.value = true

                try {
                    when (val newItemsRes = appApi.historyApi.listHistory(offset = itemsCache.size)) {
                        is ApiResult.Failure -> {
                            coroutineScope.launch {
                                showSnackbar("Cannot load history items")
                            }
                        }

                        is ApiResult.Success -> {
                            if (newItemsRes.result.isEmpty()) {
                                hasMoreItemsState.value = false
                            }
                            val lastTimestamp = itemsCache.lastOrNull()?.timestamp ?: Long.MAX_VALUE
                            itemsCache += newItemsRes.result.asReversed()
                                .asSequence().filter { it.timestamp < lastTimestamp }
                        }
                    }
                } finally {
                    isLoadingItemsState.value = false
                }
            }
        }

        fun flowToMainScreen(
            updateAppState: (ApplicationState) -> Unit,
            coroutineScope: CoroutineScope,
        ) {
            updateAppState(MainScreen(snackbarHostState, hostString, appApi, calculatorExpression))
        }

        fun removeItem(coroutineScope: CoroutineScope, id: String) {
            coroutineScope.launch {
                val itemIndex = itemsCache.indexOfFirst { it.id == id }
                if (itemIndex == -1) {
                    return@launch
                }

                when (appApi.historyApi.removeItem(id)) {
                    is ApiResult.Failure -> {
                        showSnackbar("Failed to remove history item")
                    }
                    is ApiResult.Success -> {
                        itemsCache.removeAt(itemIndex)
                    }
                }
            }
        }
    }

    companion object {
        private val expressionChars = setOf(
            *('0'..'9').toList().toTypedArray(),
            '+', '-', '*', '/', '^',
            '(', ')', '.',
        )
    }
}

suspend fun ApplicationState.showSnackbar(
    message: String,
    actionLabel: String? = null,
    duration: SnackbarDuration = SnackbarDuration.Short,
): SnackbarResult = snackbarHostState.showSnackbar(message, actionLabel, duration)
