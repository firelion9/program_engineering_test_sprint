package org.tod87et.calculator.server.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.json.Json
import org.tod87et.calculator.server.database
import org.tod87et.calculator.server.models.ComputationResult
import org.tod87et.calculator.server.models.toComputationResult

@ExperimentalSerializationApi
fun Route.historyRouting() {
    route("/history") {
        route("/list") {
            get {
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: Int.MAX_VALUE
                val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0

                try {
                    val dbResponse = database.selectFormulas(limit, offset.toLong())
                    val result = dbResponse.map {it.toComputationResult()}
                    val jsonResult = Json.encodeToString(ArraySerializer<ComputationResult, ComputationResult>
                        (ComputationResult.serializer()), result.toTypedArray())
                    return@get call.respondText(
                        jsonResult,
                        status = HttpStatusCode.OK
                    )
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
                val id = call.parameters["id"] ?: return@delete call.respondText(
                    "Missing id",
                    status = HttpStatusCode.BadRequest
                )
                //TODO
            }
        }
    }
}