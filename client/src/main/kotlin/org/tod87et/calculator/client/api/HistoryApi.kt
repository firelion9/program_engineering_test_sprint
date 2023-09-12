package org.tod87et.calculator.client.api


interface HistoryApi {
    suspend fun listHistory(fromId: String?, limit: Int = DEFAULT_LIST_LIMIT): ApiResult<List<HistoryItem>>

    suspend fun removeItem(id: String): ApiResult<Unit>

    companion object {
        private const val DEFAULT_LIST_LIMIT = 20
    }
}
