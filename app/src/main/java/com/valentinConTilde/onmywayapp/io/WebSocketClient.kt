package com.valentinConTilde.onmywayapp.io

import android.util.Log
import com.valentinConTilde.onmywayapp.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.websocket.WebSocketSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import io.ktor.client.plugins.websocket.ws
import io.ktor.http.*
import io.ktor.websocket.*


class WebSocketClient {

    private val url : String = BuildConfig.BASE_URL.replace("https://", "").replace("http://", "")

    private val client = HttpClient {
        install(WebSockets)
    }

    private lateinit var session: WebSocketSession

    // Connect to WebSocket
    fun connectToWebSocket(userId: String, onMessageReceived: (String) -> Unit) {
        val webSocketUrl = "${url}socket/tracking_updates?userId=$userId"  // Append userId as query parameter
        Log.d("WebSocket", "Connecting to WebSocket at $webSocketUrl")

        GlobalScope.launch(Dispatchers.IO) {
            try {
                client.ws(
                    method = HttpMethod.Get,
                    host = url, // replace with actual host if needed
                    //port = 443, // or another port if not using 443
                    path = "socket/tracking_updates?userId=$userId",
                ) {
                    session = this
                    Log.d("WebSocket", "Connected to WebSocket at $webSocketUrl")
                    try {
                        // Listen for incoming frames
                        for (frame in incoming) {
                            if (frame is Frame.Text) {
                                onMessageReceived(frame.readText())
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("WebSocket", "Error while receiving message: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("WebSocket", "Failed to connect: ${e.message}")
            }
        }
    }

    // Send message
    suspend fun sendMessage(message: String) {
        try {
            session.send(Frame.Text(message))
            Log.d("WebSocket", "Message sent: $message")
        } catch (e: Exception) {
            Log.e("WebSocket", "Error sending message: ${e.message}")
        }
    }

    // Close WebSocket
    fun closeWebSocket() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                session.close()
                Log.d("WebSocket", "WebSocket closed successfully")
            } catch (e: Exception) {
                Log.e("WebSocket", "Error closing WebSocket: ${e.message}")
            }
        }
    }

}