package org.tod87et.calculator.client.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.tod87et.calculator.client.ApplicationState

@Composable
fun AppRoot(
    appState: ApplicationState,
    updateAppState: (ApplicationState) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (appState) {
        is ApplicationState.ConnectionInitializationScreen -> {
            ConnectionInitializationScreen(appState, updateAppState, modifier)
        }

        is ApplicationState.MainScreen -> {
            MainScreen(appState, updateAppState, modifier)
        }

        is ApplicationState.HistoryScreen -> {
            HistoryScreen(appState, updateAppState, modifier)
        }
    }
}
