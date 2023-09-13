package org.tod87et.calculator.client

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Bestest calculator EVER",
    ) {
        App()
    }
}
