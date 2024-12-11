package com.sindesoft.onmywayapp.io

import android.util.Log
import com.sindesoft.onmywayapp.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


class WebSocketClient {

    private val url : String = BuildConfig.BASE_URL.replace("https://", "").replace("http://", "")

    private val client = HttpClient {
        install(WebSockets)
    }

    private val _messages = MutableSharedFlow<String>() // Exposes messages as a Flow
    val messages = _messages.asSharedFlow()

    private var session: WebSocketSession? = null


    suspend fun connect(){
        try {
            Log.d("WebSocketClient", "Connecting to WebSocket")
            client.webSocket(method = HttpMethod.Get, host = url, path = "socket/infinitePing") {
                session = this
                launch {
                    receiveMessages()
                }
            }
        } catch (e: Exception) {
            Log.e("WebSocketClient", "Error connecting to WebSocket: ${e.message}")
        }
    }

    private suspend fun receiveMessages() {
        try {
            session?.let { wsSession ->
                for (frame in wsSession.incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val message = frame.readText()
                            _messages.emit(message)
                        }
                        else -> Unit
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("WebSocketClient","Error receiving WebSocket messages: ${e.message}")
        }
    }

    suspend fun sendMessage(message: String) {
        try {
            session?.send(Frame.Text(message))
        } catch (e: Exception) {
            Log.e("WebSocketClient","Error sending WebSocket message: ${e.message}")
        }
    }

    suspend fun disconnect() {
        try {
            session?.close()
            session = null
        } catch (e: Exception) {
            Log.e("WebSocketClient","Error disconnecting from WebSocket: ${e.message}")
        }
    }

}