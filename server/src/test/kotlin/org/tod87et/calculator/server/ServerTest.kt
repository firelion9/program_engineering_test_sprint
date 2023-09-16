package org.tod87et.calculator.server

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.tod87et.calculator.server.database.FormulasDb
import javax.sql.DataSource
import kotlin.test.Test
import kotlin.test.assertEquals


class ServerTest {
    private val apiPath = "/api/v1"
    private val historyPath = "$apiPath/history"
    private val calculatorPath = "$apiPath/calculator"

    @Test
    fun checkHistoryRoutes() {
        testApplication {
            var response = client.get("$historyPath/list")
            assertEquals(HttpStatusCode.OK, response.status)
            response = client.delete("$historyPath/remove")
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }

    @Test
    fun checkCalculatorRoutes() {
        testApplication {
            val response = client.post("$calculatorPath/compute") {
                setBody("1+2")
            }
            assertEquals(HttpStatusCode.OK, response.status)
            val result = response.bodyAsChannel().readDouble()
            assertEquals(3.0, result, "Calculator computes incorrect")
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