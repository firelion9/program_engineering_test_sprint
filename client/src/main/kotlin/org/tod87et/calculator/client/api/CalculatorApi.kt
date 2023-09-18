package org.tod87et.calculator.client.api

interface CalculatorApi {
    suspend fun compute(expression: String): ApiResult<ComputationResult>
}
