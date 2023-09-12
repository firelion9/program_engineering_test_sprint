package org.tod87et.calculator.client.api

class LocalAppApi : AppApi {
    private val history = mutableListOf<HistoryItem>()
    override val calculatorApi: CalculatorApi = object : CalculatorApi {
        override suspend fun calculate(expression: String): ApiResult<String> {
            return if (expression.isBlank()) {
                ApiResult.Failure("expression is blank")
            } else {
                ApiResult.Success("42")
            }
        }
    }

    override val historyApi: HistoryApi = object : HistoryApi {
        override suspend fun listHistory(fromId: String?, limit: Int): ApiResult<List<HistoryItem>> = runApi {
            val startIndex = if (fromId == null) 0 else history.indexOfFirst { it.id == fromId }
            history.subList(startIndex, (startIndex + limit).coerceAtMost(history.size)).toList()
        }

        override suspend fun removeItem(id: String): ApiResult<Unit> = runApi {
            val wasRemoved = history.removeAll { it.id == id }
            if (!wasRemoved) {
                throw NoSuchElementException("No history item with id `$id`")
            }
        }

        private inline fun <T> runApi(action: () -> T) = runCatching(action).run(::mapResult)

        private fun <T> mapResult(res: Result<T>) =
            res
                .map { ApiResult.Success(it) }
                .getOrElse { ApiResult.Failure(it.message ?: "unknown exception") }

    }
}
