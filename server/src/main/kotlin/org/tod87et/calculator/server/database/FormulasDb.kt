package org.tod87et.calculator.server.database

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class FormulasDb(url: String, driver: String, user: String, password: String) {
    private val database = Database.connect(
        url = url,
        driver = driver,
        user = user,
        password = password,
    )

    init {
        transaction(database) { SchemaUtils.create(Formulas) }
    }

    fun insertFormula(formula: String, result: Double) {
        transaction(database) {
            Formulas.insert {
                it[Formulas.formula] = formula
                it[Formulas.result] = result
                it[Formulas.date] = LocalDateTime.now()
            }
        }
    }

    /**
     * Returns: true if the formula has been deleted, false if the formula is not found
     */
    fun deleteFormula(id: Int): Boolean {
        val rowsUpdated = transaction(database) {
            Formulas.deleteWhere { Formulas.id eq id }
        }

        return rowsUpdated > 0
    }

    /**
     * Returns: all formulas in ascending order by time
     */
    fun selectAllFormulas() = transaction(database) {
        Formulas.selectAll()
            .orderBy(Formulas.date, SortOrder.ASC)
            .map {
                FormulaEntry(
                    id = it[Formulas.id],
                    formula = it[Formulas.formula],
                    result = it[Formulas.result],
                    date = it[Formulas.date]
                )
            }
    }

    /**
     * Returns: the last [limit] formulas with an [offset] in ascending order by time
     */
    fun selectFormulas(limit: Int, offset: Long = 0): List<FormulaEntry> = transaction(database) {
        Formulas.selectAll()
            .orderBy(Formulas.date, SortOrder.DESC)
            .limit(limit, offset).map {
                FormulaEntry(
                    id = it[Formulas.id],
                    formula = it[Formulas.formula],
                    result = it[Formulas.result],
                    date = it[Formulas.date]
                )
            }.reversed()
    }

    private object Formulas : Table() {
        val id: Column<Int> = integer("id").autoIncrement()
        val formula: Column<String> = text("formula")
        val result: Column<Double> = double("result")
        val date: Column<LocalDateTime> = datetime("date")

        override val primaryKey = PrimaryKey(id)
    }
}