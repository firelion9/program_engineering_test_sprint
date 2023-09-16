package org.tod87et.calculator.client.composable

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.isTypedEvent
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import org.tod87et.calculator.client.ApplicationState

@Composable
fun ConnectionInitializationScreen(
    state: ApplicationState.ConnectionInitializationScreen,
    updateAppState: (ApplicationState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .focusable()
            .onPreviewKeyEvent {
                if (it.type == KeyEventType.KeyDown && it.key == Key.Enter) {
                    state.flowToMainScreen(coroutineScope, updateAppState)
                    true
                } else {
                    false
                }
            },

        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            TextField(
                value = state.hostString,
                onValueChange = { state.hostString = it },
                label = {
                    Text("Server url")
                },
                singleLine = true,
                modifier = Modifier.focusRequester(focusRequester),
            )

            Spacer(modifier = modifier.size(3.dp))

            Button(
                onClick = {
                    state.flowToMainScreen(coroutineScope, updateAppState)
                },
                enabled = !state.isConnecting
            ) {
                if (state.isConnecting) {
                    CircularProgressIndicator(Modifier.size(20.dp))
                } else {
                    Text("Connect")
                }
            }
        }
    }
}
