package org.tod87et.calculator.server.models

import kotlinx.serialization.Serializable

@Serializable
data class ComputeRequest(val expression: String)