package org.tod87et.calculator.server

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.tod87et.calculator.server.database.FormulasDb
import org.tod87et.calculator.server.models.ComputationResult
import org.tod87et.calculator.server.models.ComputeRequest
import org.tod87et.calculator.server.models.toComputationResult
import javax.sql.DataSource
import kotlin.test.*


class ServerTest {
    private val apiPath = "/api/v1"
    private val historyPath = "$apiPath/history"
    private val listPath = "$historyPath/list"
    private val removePath = "$historyPath/remove"
    private val calculatorPath = "$apiPath/calculator"
    private val computePath = "$calculatorPath/compute"

    @Test
    fun checkHistoryRoutes() {
        testApplication {
            var response = client.get(listPath)
            assertEquals(HttpStatusCode.OK, response.status)
            response = client.delete(removePath)
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }

    @Test
    fun checkCalculator_200_Ok() {
        testApplication {
            val expression = "1+2"
            val response = client.post(computePath) {
                setBody(ComputeRequest(expression))
            }
            assertEquals(HttpStatusCode.OK, response.status)
            val body = response.body<ComputationResult>()
            assertEquals(3.0, body.result, "Calculator computes incorrect")
            assertEquals(expression, body.expression, "Doesn't return correct expression")
            val entry = database.selectAllFormulas().find { it.id == body.id.toInt() }
            assertNotNull(entry, "Didn't find entry with ${body.id}")
            assertEquals(entry.toComputationResult(), body, "Entry is not the same by id=${body.id}")
        }
    }

    @Test
    fun checkCalculator_400_Bad_request() {
        testApplication {
            val expression = "*5"
            val response = client.post(computePath) {
                setBody(ComputeRequest(expression))
            }
            assertEquals(HttpStatusCode.BadRequest, response.status)
            val body = response.bodyAsText()
            //TODO add check for parse error message
            assertEquals("kjsdfk", body, "Should be equal to parse error message, TODO add this check")
        }
    }

    @Test
    fun checkHistoryGet() {
        testApplication {
            val requests = arrayOf("1 + 2" to 3, "6/2*(1+2)" to 9, "5^-1" to 0.2)
            val buffer = mutableListOf<ComputationResult>()
            requests.forEach { element ->
                val expression = element.first
                val response = client.post(computePath) {
                    ComputeRequest(expression)
                }
                assertEquals(HttpStatusCode.OK, response.status)
                buffer.add(response.body<ComputationResult>())
            }
            // ^ Preparations

            //Check GET without query
            var response = client.get(listPath)
            var list = response.body<List<ComputationResult>>()
            assertEquals(HttpStatusCode.OK, response.status)
            assertContentEquals(buffer, list)

            //Check GET with limit
            response = client.get(listPath) {
                url {
                    parameters.append("limit", "2")
                }
            }
            list = response.body<List<ComputationResult>>()
            assertEquals(HttpStatusCode.OK, response.status)
            assertContentEquals(buffer.subList(1, 3), list)

            //Check GET with offset
            response = client.get(listPath) {
                url {
                    parameters.append("offset", "1")
                }
            }
            list = response.body<List<ComputationResult>>()
            assertEquals(HttpStatusCode.OK, response.status)
            assertContentEquals(buffer.subList(0, 2), list)

            //Check GET with everything
            response = client.get(listPath) {
                url {
                    parameters.append("limit", "1")
                    parameters.append("offset", "1")
                }
            }
            list = response.body<List<ComputationResult>>()
            assertEquals(HttpStatusCode.OK, response.status)
            assertContentEquals(buffer.subList(1, 2), list)
        }
    }

    @Test
    fun checkHistoryDelete() {
        testApplication {
            val requests = arrayOf("5" to 5, "((10 + 2) / (6))" to 2, "10^0" to 1)
            val buffer = mutableListOf<ComputationResult>()
            requests.forEach { element ->
                val expression = element.first
                val response = client.post(computePath) {
                    ComputeRequest(expression)
                }
                assertEquals(HttpStatusCode.OK, response.status)
                buffer.add(response.body<ComputationResult>())
            }
            // ^ Preparations

            val order = intArrayOf(1, 0, 2)
            order.forEach { index ->
                val response = client.delete("$removePath/${buffer[index].id}")
                val body = response.body<ComputationResult>()
                assertEquals(HttpStatusCode.OK, response.status)
                assertEquals(buffer[index], body)
            }
            val response = client.get(listPath)
            val list = response.body<List<ComputationResult>>()
            assertTrue(list.isEmpty())
        }
    }

    @AfterEach
    fun clearTestDatabase() {
        database.clear()
    }

    companion object {
        private val embeddedPostgres: EmbeddedPostgres = EmbeddedPostgres.start()
        private val dataSource: DataSource = embeddedPostgres.postgresDatabase
        @JvmStatic
        @BeforeAll
        fun connectToTestDatabase() {
            database = FormulasDb(dataSource)
        }

        @JvmStatic
        @AfterAll
        fun closeTestDatabase() {
            embeddedPostgres.close()
        }
    }
}