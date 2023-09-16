package org.tod87et.calculator.server.models

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import org.tod87et.calculator.server.database.FormulaEntry

// This class will be used for communication between server and client
@Serializable
data class ComputationResult(val id: String, val expression: String, val result: Double, val timestamp: Instant)

fun FormulaEntry.toComputationResult(): ComputationResult {
    return ComputationResult(
        id = this.id.toString(),
        expression = this.formula,
        result = this.result,
        timestamp = this.date.toKotlinInstant()
    )
}