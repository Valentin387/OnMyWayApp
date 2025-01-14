package com.sindesoft.onmywayapp.ui.main.subscribers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sindesoft.onmywayapp.data.DTO.SubscriptionFetchResponse
import com.sindesoft.onmywayapp.data.repositories.SubscriptionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SubscribersViewModel (
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is subscribers Fragment"
    }
    val text: LiveData<String> = _text
    //*************************************************************

    private val _subscriptions = MutableLiveData<List<SubscriptionFetchResponse>>()
    val subscriptionList : LiveData<List<SubscriptionFetchResponse>> = _subscriptions

    // This function fetches the Subscriptions of the user
    fun fetchMySubscribers(userId: String){
        viewModelScope.launch(Dispatchers.IO) {
            val subscriptions = subscriptionRepository.fetchMySubscribers(userId) ?: emptyList()
            _subscriptions.postValue(subscriptions)
        }
    }

    // Function to remove a subscription from the list, call subscriptionRepository
    fun deleteSubscription(subscriptionId: String){
        viewModelScope.launch(Dispatchers.IO) {
            val isDeleted = subscriptionRepository.deleteSubscription(subscriptionId)
            if(isDeleted){
                // Update the LiveData to remove the item from the list
                val updatedList = _subscriptions.value?.filter { it.subscriptionId != subscriptionId }
                // ensuring the UI updates only if the API call succeeds.
                _subscriptions.postValue(updatedList!!)
            }
        }
    }
}