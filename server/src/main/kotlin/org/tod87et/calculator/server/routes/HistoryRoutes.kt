package org.tod87et.calculator.server.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.tod87et.calculator.server.database

fun Route.historyRouting() {
    route("/history") {
        get("/list") {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: Int.MAX_VALUE
            val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0

            try {
                val dbResponse = database.selectFormulas(limit, offset.toLong())
                return@get call.respond(HttpStatusCode.OK, dbResponse)
            } catch (e: Exception) {
                return@get call.respondText(
                    e.message ?: "Undefined server error",
                    status = HttpStatusCode.InternalServerError
                )
            }
        }
        delete("/remove/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respondText(
                "Id should be Int",
                status = HttpStatusCode.BadRequest
            )
            try {
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
            catch (e : Exception) {
                return@delete call.respondText(
                    "Undefined server error",
                    status = HttpStatusCode.InternalServerError
                )
            }
        }
    }
}