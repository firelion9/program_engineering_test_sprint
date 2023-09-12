package org.tod87et.calculator.client.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.tod87et.calculator.client.ApplicationState
import org.tod87et.calculator.client.api.ApiResult

@Composable
fun MainScreen(
    state: ApplicationState.MainScreen,
    updateAppState: (ApplicationState) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val coroutineScope = rememberCoroutineScope()

            TextField(
                value = state.expression,
                onValueChange = { state.expression = it },
                label = {
                    Text("Expression")
                },
                singleLine = true,
            )

            Spacer(modifier = modifier.size(3.dp))

            Button(
                onClick = {
                    state.requestCalculation(coroutineScope)
                }
            ) {
                Text("Calculate")
            }

            state.lastResult?.let { res ->
                Spacer(modifier = modifier.size(3.dp))
                Text(
                    text = when (res) {
                        is ApiResult.Success -> res.result
                        is ApiResult.Failure -> "ERROR: ${res.reason}"
                    },
                )
            }
        }
    }
}
