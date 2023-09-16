package org.tod87et.calculator.client.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import org.tod87et.calculator.client.ApplicationState

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

                LazyColumn(
                    state = state.lazyListState,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(state.historyItems, key = { it.id }) {
                        LaunchedEffect(it.id) {
                            state.onItemComposed(coroutineScope, it.id)
                        }
                        Row {
                            Column {
                                Text("Expression: ${it.expression}", style = MaterialTheme.typography.body1)
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
            }
        }
    }
}