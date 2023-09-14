package org.tod87et.calculator.server.routes

import io.ktor.server.routing.*

fun Route.historyRouting() {
    route("/history") {
        route("/list") {
            get {
                //TODO
            }
        }
        route("/remove") {
            delete("{id?}") {
                //TODO
            }
        }
    }
}