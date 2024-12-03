package com.sindesoft.onmywayapp.ui.main.subscribers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SubscribersViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is subscribers Fragment"
    }
    val text: LiveData<String> = _text
}