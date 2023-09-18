package org.tod87et.calculator.client.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints

@Composable
fun AdaptiveGrid(
    columns: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        val mesurables = subcompose("content", content)
        val rows = (mesurables.size + columns - 1) / columns
        constraints.hasBoundedWidth
        val cellConstrains = Constraints(
            minWidth = constraints.minWidth divideFinite columns,
            maxWidth = constraints.maxWidth divideFinite columns,
            minHeight = constraints.minHeight divideFinite rows,
            maxHeight = constraints.maxHeight divideFinite rows,
        )
        val placables = mesurables.map { it.measure(cellConstrains) }

        val cellWidth = placables.maxOf { it.width }
        val cellHeight = placables.maxOf { it.height }

        layout(cellWidth * columns, cellHeight * rows) {
            for (idx in placables.indices) {
                placables[idx].place(
                    idx % columns * cellWidth,
                    idx / columns * cellHeight
                )
            }
        }
    }
}

private infix fun Int.divideFinite(divisor: Int): Int = when {
    this == Constraints.Infinity -> this
    else -> this / divisor
}