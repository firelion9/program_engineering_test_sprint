package org.tod87et.calculator.server.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.tod87et.calculator.server.routes.calculatorRouting
import org.tod87et.calculator.server.routes.historyRouting


fun Application.configureRouting() {
    routing {
        route("/api/v1") {
            calculatorRouting()
            historyRouting()
        }
    }
}