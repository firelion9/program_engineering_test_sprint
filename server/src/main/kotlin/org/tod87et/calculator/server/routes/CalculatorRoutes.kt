package org.tod87et.calculator.server.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.websocket.*
import kotlinx.datetime.toKotlinInstant
import kotlinx.serialization.json.Json
import org.tod87et.calculator.server.database
import org.tod87et.calculator.server.eval
import org.tod87et.calculator.server.models.ComputationResult
import org.tod87et.calculator.server.models.toComputationResult
import java.lang.Exception
import java.sql.Timestamp


fun Route.calculatorRouting() {
    route("/calculator") {
        route("/compute") {
            post {
                val expression = call.parameters["expression"] ?: return@post call.respondText(
                    "Missing expression",
                    status = HttpStatusCode.BadRequest
                )
                val result: Double = try {
                    eval(expression)
                } catch (e : Exception) {
                    return@post call.respondText(
                        e.message ?: "Calculation failed",
                        status = HttpStatusCode.BadRequest
                    )
                }
                val response = database.insertFormula(expression, result).toComputationResult()

                val computationResult = ComputationResult(
                    response.id, response.expression, response.result, response.timestamp
                )

                return@post call.respondText(
                    Json.encodeToString(ComputationResult.serializer(), computationResult),
                    status = HttpStatusCode.OK
                )

            }
        }
    }
}