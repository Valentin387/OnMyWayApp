package com.sindesoft.onmywayapp.ui.main.subscriptions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sindesoft.onmywayapp.data.DTO.SubscriptionFetchResponse
import com.sindesoft.onmywayapp.data.repositories.SubscriptionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SubscriptionsViewModel (
    private val subscriptionRepository: SubscriptionRepository
): ViewModel() {

    //*************************************************************
    private val _text = MutableLiveData<String>().apply {
        value = "This is subscriptions Fragment"
    }
    val text: LiveData<String> = _text
    //*************************************************************

    private val _subscriptions = MutableLiveData<List<SubscriptionFetchResponse>>()
    val subscriptionList : LiveData<List<SubscriptionFetchResponse>> = _subscriptions

    // This function fetches the Subscriptions of the user
    fun fetchMySubscriptions(userId: String){
        viewModelScope.launch(Dispatchers.IO) {
            val subscriptions = subscriptionRepository.fetchMySubscriptions(userId) ?: emptyList()
            _subscriptions.postValue(subscriptions)
        }
    }
}