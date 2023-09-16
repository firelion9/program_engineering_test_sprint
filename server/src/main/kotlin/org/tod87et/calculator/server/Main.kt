package org.tod87et.calculator.server

import org.tod87et.calculator.server.database.FormulasDb

public lateinit var database: FormulasDb
fun main(args: Array<String>) {
    val databaseAddress = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost"
    val databaseDriver = System.getenv("DB_DRIVER") ?: "org.postgresql.Driver"
    val databaseUser = System.getenv("DB_USER") ?: ""
    val databasePassword = System.getenv("DB_PASSWORD") ?: ""
    database = FormulasDb(
        url = databaseAddress,
        driver = databaseDriver,
        user = databaseUser,
        password = databasePassword,
    )
    io.ktor.server.netty.EngineMain.main(args)
}

