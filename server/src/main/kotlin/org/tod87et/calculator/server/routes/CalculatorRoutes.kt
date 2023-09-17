package org.tod87et.calculator.server.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.tod87et.calculator.server.database
import org.tod87et.calculator.server.eval
import org.tod87et.calculator.server.models.ComputeRequest
import org.tod87et.calculator.server.models.toComputationResult
import kotlin.Exception


fun Route.calculatorRouting() {
    route("/calculator") {
        route("/compute") {
            post {
                val expression = call.receive<ComputeRequest>().expression
                val result: Double = try {
                    eval(expression)
                } catch (e : Exception) {
                    return@post call.respondText(
                        e.message ?: "Calculation failed",
                        status = HttpStatusCode.BadRequest
                    )
                }
                try {
                    val response = database.insertFormula(expression, result).toComputationResult()
                    return@post call.respond(HttpStatusCode.OK, response)
                }
                catch (e : Exception) {
                    if (e.message != null) {
                        val exceptionMessage = e.message ?: ""
                        return@post call.respondText(
                            exceptionMessage,
                            status = HttpStatusCode.BadRequest
                        )
                    } else {
                        return@post call.respondText(
                            "Undefined server error",
                            status = HttpStatusCode.InternalServerError
                        )
                    }
                }
            }
        }
    }
}