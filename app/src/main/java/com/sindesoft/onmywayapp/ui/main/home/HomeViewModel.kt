package com.sindesoft.onmywayapp.ui.main.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sindesoft.onmywayapp.io.WebSocketClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private val webSocketClient = WebSocketClient()

    private val _connectionStatus = MutableStateFlow(false)
    val connectionStatus: StateFlow<Boolean> = _connectionStatus

    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages: StateFlow<List<String>> = _messages

    init {
        connectToWebSocket()
        collectMessages()
    }

    private fun connectToWebSocket() {
        Log.d("HomeViewModel", "Connecting to WebSocket")
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Connecting to WebSocket")
                webSocketClient.connect()
                _connectionStatus.value = true
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error connecting to WebSocket: ${e.message}")
                _connectionStatus.value = false
            }
        }
    }

    private fun collectMessages() {
        Log.d("HomeViewModel", "Collecting messages")
        viewModelScope.launch {
            webSocketClient.messages
                .collect { message ->
                    _messages.value += message
                }
        }
    }

    fun sendMessage(message: String) {
        Log.d("HomeViewModel", "Sending message: $message")
        viewModelScope.launch {
            webSocketClient.sendMessage(message)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("HomeViewModel", "Disconnecting from WebSocket")
        viewModelScope.launch {
            webSocketClient.disconnect()
        }
    }



}