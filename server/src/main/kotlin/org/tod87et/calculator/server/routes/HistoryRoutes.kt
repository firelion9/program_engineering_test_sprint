package org.tod87et.calculator.server.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.tod87et.calculator.server.database
import org.tod87et.calculator.server.models.toComputationResult

fun Route.historyRouting() {
    route("/history") {
        route("/list") {
            get {
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: Int.MAX_VALUE
                val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0

                try {
                    val dbResponse = database.selectFormulas(limit, offset.toLong())
                    val result = dbResponse.map {it.toComputationResult()}
                    return@get call.respond(HttpStatusCode.OK, result)
                } catch (e: Exception) {
                    return@get call.respondText(
                        e.message ?: "Undefined server error",
                        status = HttpStatusCode.InternalServerError
                    )
                }


            }
        }
        route("/remove") {
            delete("{id?}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respondText(
                    "Missing id",
                    status = HttpStatusCode.BadRequest
                )
                if(database.deleteFormula(id)) {
                    return@delete call.respondText(
                        "OK",
                        status = HttpStatusCode.OK
                    )
                }
                else {
                    return@delete call.respondText(
                        "Formula not found",
                        status = HttpStatusCode.NotFound
                    )
                }

            }
        }
    }
}