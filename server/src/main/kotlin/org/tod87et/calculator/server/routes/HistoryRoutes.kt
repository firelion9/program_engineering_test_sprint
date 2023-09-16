package org.tod87et.calculator.server.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import org.tod87et.calculator.server.database

fun Route.historyRouting() {
    route("/history") {
        route("/list") {
            get() {
                val limit = call.request.queryParameters["limit"]?.toIntOrNull()
                val offset = call.request.queryParameters["offset"]?.toIntOrNull()
                //TODO
            }
        }
        route("/remove") {
            delete("{id?}") {
                val id = call.parameters["id"] ?: return@delete call.respondText(
                    "Missing id",
                    status = HttpStatusCode.BadRequest
                )
                //TODO
            }
        }
    }
}