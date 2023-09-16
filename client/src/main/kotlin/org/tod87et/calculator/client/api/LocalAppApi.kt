package org.tod87et.calculator.client.api

class LocalAppApi : AppApi {
    private val history = mutableListOf<ComputationResult>()
    private var lastId = 0L
    override val calculatorApi: CalculatorApi = object : CalculatorApi {
        override suspend fun compute(expression: String): ApiResult<ComputationResult> {
            return if (expression.isBlank()) {
                ApiResult.Failure("expression is blank")
            } else {
                val res = ComputationResult(lastId++.toString(), expression, 42.0, System.currentTimeMillis())
                history.add(res)
                ApiResult.Success(res)
            }
        }
    }

    override val historyApi: HistoryApi = object : HistoryApi {
        override suspend fun listHistory(offset: Int, limit: Int): ApiResult<List<ComputationResult>> = runApi {
            val fromIndex = (history.size - offset - limit).coerceAtLeast(0)
            history.subList(fromIndex, (fromIndex + limit).coerceAtMost(history.size)).toList()
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
