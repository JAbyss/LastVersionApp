package com.foggyskies.petapp.presentation.ui.authorization.client

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

val clientJson
    get() = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json)
        }
        expectSuccess = false
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
        }
    }