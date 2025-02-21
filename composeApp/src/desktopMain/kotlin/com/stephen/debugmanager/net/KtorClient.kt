package com.stephen.debugmanager.net

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlin.time.Duration

class KtorClient {

    val client = HttpClient(CIO) {
        install(Logging) {
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(WebSockets) {
            pingInterval = Duration.parse("15s")
        }
    }

    suspend fun connectWebSocket(url: String, block: suspend  DefaultClientWebSocketSession.() -> Unit) {
        client.webSocket(url) {
            block(this)
        }
    }

    fun release() {
        client.close()
    }
}
