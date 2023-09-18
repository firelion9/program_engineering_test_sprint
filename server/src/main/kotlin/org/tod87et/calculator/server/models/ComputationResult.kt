package org.tod87et.calculator.server.models

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import org.tod87et.calculator.server.database.FormulaEntry
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

const val EPS_RESULT = 1e-6
val EPS_DURATION = 10.milliseconds

// This class will be used for communication between server and client
@Serializable
data class ComputationResult(val id: String, val expression: String, val result: Double, val timestamp: Instant) {
    override operator fun equals(other: Any?): Boolean {
        return when (other) {
            is ComputationResult -> {
                (id == other.id
                        && expression == other.expression
                        && abs(result - other.result) < EPS_RESULT
                        && (timestamp - other.timestamp).absoluteValue < EPS_DURATION)
            }
            else -> false
        }
    }
}

fun FormulaEntry.toComputationResult(): ComputationResult = ComputationResult(
    id = this.id.toString(),
    expression = this.formula,
    result = this.result,
    timestamp = this.date.toKotlinInstant()
)