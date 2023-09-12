package org.tod87et.calculator.client

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.tod87et.calculator.client.composable.AppRoot

@Composable
@Preview
fun App() {
    val scaffoldState = rememberScaffoldState()

    MaterialTheme {
        Scaffold(
            scaffoldState = scaffoldState
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                val (appState, updateAppState) = remember {
                    mutableStateOf<ApplicationState>(
                        ApplicationState.ConnectionInitializationScreen(scaffoldState.snackbarHostState)
                    )
                }

                AppRoot(appState, updateAppState)
            }
        }
    }
}
