package org.tod87et.calculator.server.routes

import io.ktor.server.routing.*
import org.tod87et.calculator.server.database


fun Route.calculatorRouting() {
    route("/calculator") {
        route("/compute") {
            post {
                /* TODO
                 * Как я понимаю, парсинг и подсчет будет происходить с помощью
                 * Parser.eval(expression)
                 * Нужно тут еще получить правильно body
                 */
            }
        }
    }
}