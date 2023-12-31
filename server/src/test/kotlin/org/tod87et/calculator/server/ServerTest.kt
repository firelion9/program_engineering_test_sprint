package org.tod87et.calculator.server

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.tod87et.calculator.server.database.FormulasDb
import org.tod87et.calculator.shared.models.ComputationResult
import org.tod87et.calculator.shared.models.ComputeRequest
import javax.sql.DataSource
import kotlin.test.*


class ServerTest {
    private val apiPath = "/api/v1"
    private val historyPath = "$apiPath/history"
    private val listPath = "$historyPath/list"
    private val removePath = "$historyPath/remove"
    private val calculatorPath = "$apiPath/calculator"
    private val computePath = "$calculatorPath/compute"

    private inline fun testJsonApplication(crossinline body: suspend ApplicationTestBuilder.(HttpClient) -> Unit) = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        body(client)
    }

    @Test
    fun checkHistoryRoutes() = testJsonApplication { client ->
        var response = client.get(listPath)
        assertEquals(HttpStatusCode.OK, response.status)
        response = client.delete(removePath)
        assertEquals(HttpStatusCode.NotFound, response.status)
        response = client.delete("$removePath/aaaa")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun checkCalculator_200_Ok() = testJsonApplication { client->
        val expression = "1+2"
        val response = client.post(computePath) {
            contentType(ContentType.Application.Json)
            setBody(ComputeRequest(expression))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.body<ComputationResult>()
        //assertEquals(3.0, body.result, "Calculator computes incorrect")
        assertEquals(expression, body.expression, "Doesn't return correct expression")
        val entry = database.selectAllFormulas().find { it.id == body.id }
        assertNotNull(entry, "Didn't find entry with ${body.id}")
        assertEquals(entry, body, "Entry is not the same by id=${body.id}")
    }

    @Test
    fun checkHistoryGet() = testJsonApplication { client ->
        val requests = arrayOf("1 + 2" to 3.0, "6/2*(1+2)" to 9.0, "5^-1" to 0.2)
        val buffer = mutableListOf<ComputationResult>()
        requests.forEach { element ->
            val expression = element.first
            val response = client.post(computePath) {
                contentType(ContentType.Application.Json)
                setBody(ComputeRequest(expression))
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

    @Test
    fun checkHistoryDelete() = testJsonApplication { client ->
        val requests = arrayOf("5" to 5.0, "((10 + 2) / (6))" to 2.0, "10^0" to 1.0)
        val buffer = mutableListOf<ComputationResult>()
        requests.forEach { element ->
            val expression = element.first
            val response = client.post(computePath) {
                contentType(ContentType.Application.Json)
                setBody(ComputeRequest(expression))
            }
            assertEquals(HttpStatusCode.OK, response.status)
            buffer.add(response.body<ComputationResult>())
        }
        // ^ Preparations

        val order = listOf(1, 0, 2)
        order.forEachIndexed { round, index ->
            var response = client.delete("$removePath/${buffer[index].id}")
            assertEquals(HttpStatusCode.OK, response.status)
            response = client.get(listPath)
            val list = response.body<List<ComputationResult>>()
            assertContentEquals(buffer.slice(order.subList(round + 1, order.size).sorted()), list)
        }
        val response = client.get(listPath)
        val list = response.body<List<ComputationResult>>()
        assertTrue(list.isEmpty())
    }

    @Test
    fun checkParserResult() = testJsonApplication { client ->
        val requests = arrayOf(
            "1 + 2" to 3.0, "6/2*(1+2)" to 9.0, "5^-1" to 0.2,
            "5" to 5.0, "((10 + 2) / (6))" to 2.0, "10^0" to 1.0,
        )
        val buffer = mutableListOf<ComputationResult>()
        requests.forEach { element ->
            val expression = element.first
            val response = client.post(computePath) {
                contentType(ContentType.Application.Json)
                setBody(ComputeRequest(expression))
            }
            assertEquals(HttpStatusCode.OK, response.status)
            buffer.add(response.body<ComputationResult>())
        }
        for (index in requests.indices) {
            assertEquals(requests[index].second, buffer[index].result,
                "Parser is not working for ${requests[index].first}")
        }
    }

    @Test
    fun checkParserBadResult() = testJsonApplication { client ->
        val expression = "*5"
        val response = client.post(computePath) {
            contentType(ContentType.Application.Json)
            setBody(ComputeRequest(expression))
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        var message = ""
        try {
            eval(expression)
        } catch (e: Exception) {
            message = e.message ?: "Undefined server error"
        }

        val body = response.bodyAsText()
        assertEquals(message, body, "Should be equal to parse error message")
    }

    @Test
    fun checkESupport() = testJsonApplication { client ->
        val requests = arrayOf(
            "1E5" to 1e5, "2E3*10" to 2e4, "1E-1" to 1e-1, "-1.2457E-2" to -0.012457,
            "2^1E1" to 1024.0, "9E3/2E2" to 4.5e1, "1E2+3-2E1" to 83.0
        )
        val buffer = mutableListOf<ComputationResult>()
        requests.forEach { element ->
            val expression = element.first
            val response = client.post(computePath) {
                contentType(ContentType.Application.Json)
                setBody(ComputeRequest(expression))
            }
            assertEquals(HttpStatusCode.OK, response.status)
            buffer.add(response.body<ComputationResult>())
        }
        for (index in requests.indices) {
            assertEquals(requests[index].second, buffer[index].result,
                "Parser is not working for ${requests[index].first}")
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