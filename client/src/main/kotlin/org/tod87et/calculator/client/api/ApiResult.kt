package org.tod87et.calculator.client.api

sealed interface ApiResult<out T> {
    data class Success<out T>(val result: T) : ApiResult<T>

    data class Failure(val reason: String) : ApiResult<Nothing>
}
