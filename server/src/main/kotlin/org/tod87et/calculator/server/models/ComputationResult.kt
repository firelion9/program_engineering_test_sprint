package org.tod87et.calculator.server.models

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

// This class will be used for communication between server and client
@Serializable
data class ComputationResult(val id: String, val expression: String, val result: Double, val timestamp: Instant)