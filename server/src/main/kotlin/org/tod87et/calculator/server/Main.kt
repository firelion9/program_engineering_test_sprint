package org.tod87et.calculator.server

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required

const val DEFAULT_PORT = 8080
const val DEFAULT_ADDRESS = "127.0.0.1"

fun main(args: Array<String>) {
    val parser = ArgParser("Calculator Server")
    val ipAddress by parser.option(ArgType.String,
        fullName = "address",
        description = "ip address for server"
    ).default(DEFAULT_ADDRESS)
    val port by parser.option(ArgType.Int,
        fullName = "port",
        description = "port for server"
    ).default(DEFAULT_PORT)
    val databaseAddress by parser.option(ArgType.String,
        fullName = "db_url",
        description = "Database url to connect to"
    ).required()
    val databaseDriver by parser.option(ArgType.String,
        fullName = "db_driver",
        description = "Database driver for correct connection"
    ).default("org.postgresql.Driver")
    val databaseUser by parser.option(ArgType.String,
        fullName = "db_user",
        description = "Database user for login").default("")
    val databasePassword by parser.option(ArgType.String,
        fullName = "db_password",
        description = "Database password for login").default("")
    parser.parse(args)
}