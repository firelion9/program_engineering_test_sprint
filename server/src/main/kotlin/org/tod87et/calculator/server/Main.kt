package org.tod87et.calculator.server

import io.ktor.server.netty.*
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import org.tod87et.calculator.server.database.FormulasDb
import javax.sql.DataSource

public lateinit var database: FormulasDb
fun main(args: Array<String>) {
    val databaseAddress = System.getenv("DB_URL")
    val databaseDriver = System.getenv("DB_DRIVER") ?: "org.postgresql.Driver"
    val databaseUser = System.getenv("DB_USER") ?: ""
    val databasePassword = System.getenv("DB_PASSWORD") ?: ""
    var embeddedPostgres: EmbeddedPostgres? = null
    var dataSource: DataSource? = null
    if (databaseAddress == null) {
        embeddedPostgres = EmbeddedPostgres.start()
        dataSource = embeddedPostgres.postgresDatabase
        database = FormulasDb(dataSource)
    } else {
        database = FormulasDb(
            url = databaseAddress,
            driver = databaseDriver,
            user = databaseUser,
            password = databasePassword,
        )
    }
    EngineMain.main(args)
    embeddedPostgres?.close()
}

