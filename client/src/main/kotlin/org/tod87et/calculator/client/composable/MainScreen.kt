package org.tod87et.calculator.client.composable

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.unit.dp
import org.tod87et.calculator.client.ApplicationState
import org.tod87et.calculator.client.api.ApiResult

@Composable
fun MainScreen(
    state: ApplicationState.MainScreen,
    updateAppState: (ApplicationState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
    }

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .focusable()
            .onPreviewKeyEvent {
                if (it.type != KeyEventType.KeyDown) {
                    return@onPreviewKeyEvent false
                }

                when {
                    it.key == Key.Enter || it.utf16CodePoint.toChar() == '=' -> {
                        state.requestCalculation(coroutineScope)
                        true
                    }

                    else -> {
                        false
                    }
                }
            }
            .onKeyEvent {
                if (it.type != KeyEventType.KeyDown) {
                    return@onKeyEvent false
                }
                when (val char = it.utf16CodePoint.toChar()) {
                    in '0'..'9', '+', '-', '*', '/', '^', '(', ')' -> {
                        state.expression += char
                        return@onKeyEvent true
                    }

                }

                when (it.key) {
                    Key.Backspace -> {
                        state.onBackSpace()
                        true
                    }

                    Key.Enter -> {
                        state.requestCalculation(coroutineScope)
                        true
                    }

                    else -> {
                        false
                    }
                }
            }
    ) {
        Column {

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        state.flowToConnectionInitializationScreen(updateAppState, coroutineScope)
                    }
                ) {
                    Image(Icons.Outlined.ArrowBack, "back")
                }

                Text("Current server: ${state.hostString}")
            }

            Spacer(modifier.weight(1f))

            Column(
                modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextField(
                    value = state.expression,
                    onValueChange = {
                        state.expression = it
                        state.lastResult = null
                    },
                    label = {
                        Text("Expression")
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                )
                TextField(
                    value = when (val res = state.lastResult) {
                        null -> ""
                        is ApiResult.Success -> res.result.result.toString()
                        is ApiResult.Failure -> "ERROR: ${res.reason}"
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text("Result")
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                NumPad(
                    isComputing = remember {
                        derivedStateOf {
                            state.isWaitingForResponse
                        }
                    },
                    onSymbols = {
                        state.expression += it
                    },
                    onBackspace = state::onBackSpace,
                    onCompute = {
                        state.requestCalculation(coroutineScope)
                    }
                )
            }

            Spacer(modifier.weight(1f))
        }
    }
}

@Composable
private fun NumPad(
    modifier: Modifier = Modifier,
    isComputing: State<Boolean>,
    onBackspace: (clear: Boolean) -> Unit,
    onSymbols: (String) -> Unit,
    onCompute: () -> Unit,
) {
    Column(modifier = modifier) {
        Row {
            NumButton("(", onSymbols = onSymbols)
            NumButton(")", onSymbols = onSymbols)
            NumButton(
                "\u232B", // ⌫
                onClick = {
                    onBackspace(false)
                }
            )
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onCompute,
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.secondaryVariant
                ),
                enabled = !isComputing.value
            ) {
                if (isComputing.value) {
                    CircularProgressIndicator(modifier = Modifier.size(30.dp))
                } else {
                    Text("=", style = MaterialTheme.typography.h5)
                }
            }

        }
        Row {
            NumButton("+", onSymbols = onSymbols)
            NumButton("7", onSymbols = onSymbols)
            NumButton("8", onSymbols = onSymbols)
            NumButton("9", onSymbols = onSymbols)
        }
        Row {
            NumButton("-", onSymbols = onSymbols)
            NumButton("4", onSymbols = onSymbols)
            NumButton("5", onSymbols = onSymbols)
            NumButton("6", onSymbols = onSymbols)
        }
        Row {
            NumButton("*", onSymbols = onSymbols)
            NumButton("1", onSymbols = onSymbols)
            NumButton("2", onSymbols = onSymbols)
            NumButton("3", onSymbols = onSymbols)
        }
        Row {
            NumButton("/", onSymbols = onSymbols)
            NumButton("^", onSymbols = onSymbols)
            NumButton("0", onSymbols = onSymbols)
            NumButton(".", onSymbols = onSymbols)
        }
    }
}

@Composable
private fun RowScope.NumButton(text: String, onSymbols: (String) -> Unit) {
    NumButton(
        text = text,
        onClick = {
            onSymbols(text)
        }
    )
}

@Composable
private fun RowScope.NumButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        modifier = Modifier.weight(1f),
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.secondaryVariant
        )
    ) {
        Text(text, style = MaterialTheme.typography.h5)
    }
}


@Preview
@Composable
private fun NumPadPreview() {
    MaterialTheme {
        NumPad(
            isComputing = remember { mutableStateOf(false) },
            onSymbols = {},
            onBackspace = {},
            onCompute = {},
        )
    }
}