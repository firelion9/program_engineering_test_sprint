package org.tod87et.calculator.server.routes

import io.ktor.server.routing.*

fun Route.calculatorRouting() {
    route("/calculator") {
        route("/compute") {
            post {
                //TODO
            }
        }
    }
}