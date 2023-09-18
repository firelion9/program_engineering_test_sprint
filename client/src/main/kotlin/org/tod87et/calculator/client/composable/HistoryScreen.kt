package org.tod87et.calculator.client.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import org.tod87et.calculator.client.ApplicationState
import org.tod87et.calculator.client.md_theme_light_secondaryContainer
import org.tod87et.calculator.shared.models.ComputationResult

@Composable
fun HistoryScreen(
    state: ApplicationState.HistoryScreen,
    updateAppState: (ApplicationState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(state) {
        state.loadMoreItems(coroutineScope)
    }

    Box(
        modifier = modifier
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        state.flowToMainScreen(updateAppState, coroutineScope)
                    }
                ) {
                    Image(Icons.Outlined.ArrowBack, "back")
                }

                Text("Current server: ${state.hostString}")
            }

            Column(
                modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("History", style = MaterialTheme.typography.h4)
                Spacer(modifier = Modifier.size(3.dp))

                when {
                    state.historyItems.isNotEmpty() -> {
                        LazyColumn(
                            state = state.lazyListState,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            items(state.historyItems, key = { it.id }) {
                                HistoryItem(it, state, coroutineScope, modifier = Modifier.padding(2.dp))
                            }
                        }
                    }

                    state.isLoadingItems -> {
                        CircularProgressIndicator(modifier = Modifier.size(100.dp))
                    }

                    else -> {
                        Text("History is empty", style = MaterialTheme.typography.subtitle2)
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(
    it: ComputationResult,
    state: ApplicationState.HistoryScreen,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(it.id) {
        state.onItemComposed(coroutineScope, it.id)
    }
    Surface(color = md_theme_light_secondaryContainer, modifier = modifier, shape = MaterialTheme.shapes.medium) {
        Row(modifier = Modifier.padding(4.dp)) {
            Column {
                Text(
                    "Expression: ${it.expression}",
                    style = MaterialTheme.typography.body1
                )
                Text("Result: ${it.result}", style = MaterialTheme.typography.body2)
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    state.removeItem(coroutineScope, it.id)
                }
            ) {
                Image(
                    Icons.Outlined.Close,
                    "remove"
                )
            }
        }
    }
}