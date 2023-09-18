package org.tod87et.calculator.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class ComputeRequest(val expression: String)