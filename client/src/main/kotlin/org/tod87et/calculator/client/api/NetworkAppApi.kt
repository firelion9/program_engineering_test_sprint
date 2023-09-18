package org.tod87et.calculator.client.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.http.path
import kotlinx.serialization.Serializable
import org.tod87et.calculator.shared.models.ComputationResult
import org.tod87et.calculator.shared.models.ComputeRequest
import java.util.logging.Level
import java.util.logging.Logger

class NetworkAppApi(
    private val serverUrl: String,
    private val httpClient: HttpClient,
) : AppApi {
    private val logger = Logger.getLogger("NetworkApi")

    override val calculatorApi: CalculatorApi = object : CalculatorApi {
        override suspend fun compute(expression: String): ApiResult<ComputationResult> {
            return try {
                val response = httpClient.request {
                    setUrl {
                        path(COMPUTE_PATH)
                    }
                    method = HttpMethod.Post
                    contentType(ContentType.Application.Json)
                    setBody(ComputeRequest(expression))
                }

                parseResponse(response)
            } catch (e: Exception) {
                logger.log(Level.WARNING, "Server is unavailable", e)
                ApiResult.Failure("Server is unavailable")
            }
        }
    }

    override val historyApi: HistoryApi = object : HistoryApi {
        override suspend fun listHistory(offset: Int, limit: Int): ApiResult<List<ComputationResult>> {
            return try {
                val response = httpClient.request {
                    setUrl {
                        path(HISTORY_LIST_PATH)
                        parameters.run {
                            append("limit", limit.toString())
                            append("offset", offset.toString())
                        }
                    }
                    method = HttpMethod.Get
                }

                parseResponse(response)
            } catch (e: Exception) {
                logger.log(Level.WARNING, "Server is unavailable", e)
                ApiResult.Failure("Server is unavailable")
            }
        }

        override suspend fun removeItem(id: String): ApiResult<Unit> {
            return try {
                val response = httpClient.request {
                    setUrl {
                        path(HISTORY_REMOVE_PATH, id)
                    }
                    method = HttpMethod.Delete
                }

                if (response.status.isSuccess()) {
                    return ApiResult.Success(Unit)
                } else {
                    return ApiResult.Failure(response.bodyAsText())
                }
            } catch (e: Exception) {
                logger.log(Level.WARNING, "Server is unavailable", e)
                ApiResult.Failure("Server is unavailable")
            }
        }
    }

    private suspend inline fun <reified B> parseResponse(response: HttpResponse): ApiResult<B> {
        return try {
            if (response.status.isSuccess()) {
                ApiResult.Success(response.body<B>())
            } else {
                ApiResult.Failure(response.bodyAsText())
            }
        } catch (e: Exception) {
            logger.log(Level.WARNING, "Cannot parse server response", e)
            ApiResult.Failure("Cannot parse server response")
        }
    }

    private fun HttpRequestBuilder.setUrl(builder: URLBuilder.() -> Unit) {
        URLBuilder(serverUrl)
            .apply(builder)
            .let { url(it.build()) }
    }

    companion object {
        private const val COMPUTE_PATH = "api/v1/calculator/compute"
        private const val HISTORY_LIST_PATH = "api/v1/history/list"
        private const val HISTORY_REMOVE_PATH = "api/v1/history/remove"
    }
}