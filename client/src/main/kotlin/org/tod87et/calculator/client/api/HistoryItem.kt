package org.tod87et.calculator.client.api

data class HistoryItem(
    val id: String,
    val expression: String,
    val result: ApiResult<String>,
)
