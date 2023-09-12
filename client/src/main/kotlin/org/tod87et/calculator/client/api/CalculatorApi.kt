package org.tod87et.calculator.client.api

interface CalculatorApi {
    suspend fun calculate(expression: String): ApiResult<String>
}
