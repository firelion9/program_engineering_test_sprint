package org.tod87et.calculator.server.models

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

@Serializable
data class ComputationResult(val id: Int, val formula: String, val result: Double, val date: Instant)