package com.foggyskies.petapp.presentation.ui.authorization.client

import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
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

val HttpRequestBuilder.AuthHeader
    get() = header(BuildConfig.Authorization, MainPreference.Token)
