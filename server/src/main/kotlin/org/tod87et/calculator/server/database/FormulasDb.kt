package org.tod87et.calculator.server.database

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import javax.sql.DataSource

class FormulasDb {
    private val database: Database

    constructor(url: String, driver: String, user: String, password: String) {
        database = Database.connect(
            url = url,
            driver = driver,
            user = user,
            password = password,
        )
        transaction(database) { SchemaUtils.create(Formulas) }
    }

    constructor(datasource: DataSource) {
        database = Database.connect(datasource)
        transaction(database) { SchemaUtils.create(Formulas) }
    }

    fun insertFormula(formula: String, result: Double): FormulaEntry {
        val date = Instant.now()
        var id: Int = -1
        transaction(database) {
            id = Formulas.insert {
                it[Formulas.formula] = formula
                it[Formulas.result] = result
                it[Formulas.date] = date
            } get Formulas.id
        }
        return FormulaEntry(id, formula, result, date)
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

    fun clear(): Unit = transaction(database) {
        Formulas.deleteAll()
    }

    object Formulas : Table() {
        val id: Column<Int> = integer("id").autoIncrement()
        val formula: Column<String> = text("formula")
        val result: Column<Double> = double("result")
        val date: Column<Instant> = timestamp("date")

        override val primaryKey = PrimaryKey(id)
    }
}