package org.tod87et.calculator.server

import io.ktor.server.application.*
import org.tod87et.calculator.server.plugins.configureRouting
import org.tod87et.calculator.server.plugins.configureSerialization

fun Application.module() {
    configureRouting()
    configureSerialization()
}