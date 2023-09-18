package org.tod87et.calculator.client.api

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ComputationResult(
    val id: String,
    val expression: String,
    val result: Double,
    val timestamp: Instant,
)