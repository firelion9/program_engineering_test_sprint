package org.tod87et.calculator.client.api

import org.tod87et.calculator.shared.models.ComputationResult

interface CalculatorApi {
    suspend fun compute(expression: String): ApiResult<ComputationResult>
}
