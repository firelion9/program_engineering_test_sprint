package org.tod87et.calculator.client.api

import org.tod87et.calculator.shared.models.ComputationResult

interface HistoryApi {
    suspend fun listHistory(offset: Int = 0, limit: Int = DEFAULT_LIST_LIMIT): ApiResult<List<ComputationResult>>

    suspend fun removeItem(id: String): ApiResult<Unit>

    companion object {
        private const val DEFAULT_LIST_LIMIT = 20
    }
}
