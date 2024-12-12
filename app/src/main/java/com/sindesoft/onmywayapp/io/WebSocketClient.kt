package com.sindesoft.onmywayapp.io

import android.util.Log
import com.sindesoft.onmywayapp.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.websocket.WebSocketSession
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext
import io.ktor.client.*
import io.ktor.client.plugins.websocket.ws
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.websocket.*


class WebSocketClient {

    private val url : String = BuildConfig.BASE_URL.replace("https://", "").replace("http://", "")

    private val client = HttpClient {
        install(WebSockets)
    }

    private lateinit var session: WebSocketSession

    // Connect to WebSocket
    fun connectToWebSocket(onMessageReceived: (String) -> Unit) {
        Log.d("WebSocket", "Connecting to WebSocket at $url")
        GlobalScope.launch(Dispatchers.IO) {
            try {
                client.ws(
                    method = HttpMethod.Get,
                    host = url, // replace with actual host if needed
                    //port = 443, // or another port if not using 443
                    path = "/socket/infinitePing"
                ) {
                    session = this
                    Log.d("WebSocket", "Connected to WebSocket at $url")
                    try {
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