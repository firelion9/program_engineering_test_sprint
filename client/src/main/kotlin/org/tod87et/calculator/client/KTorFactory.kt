package org.tod87et.calculator.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

object KTorFactory {
    fun createHttpClient(): HttpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json()
        }
    }
}