package com.valentinConTilde.onmywayapp.ui.main.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class AddSubscriptionViewModel : ViewModel() {
    // MutableLiveData to hold the code of the subscription
    private val _code = MutableLiveData<String?>()
    val code: LiveData<String?>
        get() = _code

    // Function to update the code
    fun reportCode(code: String) {
        _code.value = code
    }

    // Function to reset the value after handling
    fun resetCode() {
        _code.value = null
    }
}