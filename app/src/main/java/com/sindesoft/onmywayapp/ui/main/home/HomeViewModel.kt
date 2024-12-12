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

    private val _messages = MutableLiveData<MutableList<String>>()
    val messages: LiveData<MutableList<String>> = _messages

    init {
        _messages.value = mutableListOf() // Initialize the messages list
    }

    private fun addMessage(message: String) {
        // Add the new message to the list
        val currentMessages = _messages.value ?: mutableListOf()
        currentMessages.add(message)
        _messages.value = currentMessages
    }

    fun getMessageCount(): Int {
        return _messages.value?.size ?: 0
    }

}