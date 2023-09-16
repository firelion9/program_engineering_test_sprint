package org.tod87et.calculator.server.database

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.sql.DataSource

class FormulasDbTest {
    private val formulas = listOf(
        "1 * 5 - 1" to 4.0,
        "3 * 5 ^ 2" to 75.0,
        "4 - 2 * 3" to -2.0,
        "10 * 3 - 4 ^ 2" to 14.0
    )

    private fun List<FormulaEntry>.parse() = map { it.formula to it.result }

    @BeforeEach
    fun insertFormulas() {
        formulas.forEach { (formula, result) ->
            database.insertFormula(formula, result)
        }
    }

    @AfterEach
    fun clear() = database.clear()

    @Test
    fun selectAllTest() {
        val actual = database.selectAllFormulas().parse()
        assertEquals(formulas, actual)
    }

    @Test
    fun clearTest() {
        assertEquals(formulas, database.selectAllFormulas().parse())
        database.clear()
        assertTrue(database.selectAllFormulas().isEmpty())
    }

    @Test
    fun insertTest() {
        val (formula, result) = "1 - 1" to 0.0
        database.insertFormula(formula, result)

        val actual = database.selectAllFormulas().parse()
        val expected = formulas + (formula to result)

        assertEquals(expected, actual)
    }

    @Test
    fun selectTest() {
        val actual = database.selectFormulas(2, 1).parse()
        val expected = formulas.dropLast(1).takeLast(2)

        assertEquals(expected, actual)
    }

    @Test
    fun selectWithBigLimitTest() {
        val actual = database.selectFormulas(1000, 1).parse()
        val expected = formulas.dropLast(1)

        assertEquals(expected, actual)
    }

    @Test
    fun deleteTest() {
        database.deleteFormula(database.selectFormulas(1).single().id)

        val actual = database.selectAllFormulas().parse()
        val expected = formulas.dropLast(1)

        assertEquals(expected, actual)
    }

    companion object {
        private val embeddedPostgres: EmbeddedPostgres = EmbeddedPostgres.start()
        private val dataSource: DataSource = embeddedPostgres.postgresDatabase

        val database: FormulasDb = FormulasDb(dataSource)

        @JvmStatic
        @AfterAll
        fun shutdown() {
            embeddedPostgres.close()
        }
    }
}