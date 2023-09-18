package org.tod87et.calculator.server.database

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.tod87et.calculator.shared.models.ComputationResult
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

    fun insertFormula(formula: String, result: Double): ComputationResult {
        val date = Clock.System.now()
        var id: Int = -1
        transaction(database) {
            id = Formulas.insert {
                it[Formulas.expression] = formula
                it[Formulas.result] = result
                it[Formulas.timestamp] = date
            } get Formulas.id
        }
        return ComputationResult(id.toString(), formula, result, date)
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
            .orderBy(Formulas.timestamp, SortOrder.ASC)
            .map {
                ComputationResult(
                    id = it[Formulas.id].toString(),
                    expression = it[Formulas.expression],
                    result = it[Formulas.result],
                    timestamp = it[Formulas.timestamp]
                )
            }
    }

    /**
     * Returns: the last [limit] formulas with an [offset] in ascending order by time
     */
    fun selectFormulas(limit: Int, offset: Long = 0): List<ComputationResult> = transaction(database) {
        Formulas.selectAll()
            .orderBy(Formulas.timestamp, SortOrder.DESC)
            .limit(limit, offset).map {
                ComputationResult(
                    id = it[Formulas.id].toString(),
                    expression = it[Formulas.expression],
                    result = it[Formulas.result],
                    timestamp = it[Formulas.timestamp]
                )
            }.reversed()
    }

    fun clear(): Unit = transaction(database) {
        Formulas.deleteAll()
    }

    object Formulas : Table() {
        val id: Column<Int> = integer("id").autoIncrement()
        val expression: Column<String> = text("formula")
        val result: Column<Double> = double("result")
        val timestamp: Column<Instant> = timestamp("date")

        override val primaryKey = PrimaryKey(id)
    }
}