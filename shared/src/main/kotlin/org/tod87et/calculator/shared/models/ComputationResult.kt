package org.tod87et.calculator.shared.models

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

const val EPS_RESULT = 1e-6
val EPS_DURATION = 10.milliseconds

// This class will be used for communication between server and client
@Serializable
data class ComputationResult(val id: String, val expression: String, val result: Double, val timestamp: Instant) {
    override operator fun equals(other: Any?): Boolean {
        if (other !is ComputationResult)
            return false
        return (id == other.id
                    && expression == other.expression
                    && abs(result - other.result) < EPS_RESULT
                    && (timestamp - other.timestamp).absoluteValue < EPS_DURATION)
    }
}