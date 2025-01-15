package com.valentinConTilde.onmywayapp.data.repositories

import android.util.Log
import com.valentinConTilde.onmywayapp.data.DTO.NewSubscriptionRequest
import com.valentinConTilde.onmywayapp.data.DTO.SubscriptionFetchResponse
import com.valentinConTilde.onmywayapp.io.SubscriptionService

class SubscriptionRepository (
    private val subscriptionService: SubscriptionService // Retrofit service
) {
    //Fetch the subscriptions of the user
    suspend fun fetchMySubscriptions(userId: String): List<SubscriptionFetchResponse>? {
        return try{
            val response = subscriptionService.fetchMySubscriptions(userId)
            if (response.isSuccessful) {
                Log.d("SubscriptionRepository", "Subscriptions fetched successfully")
                Log.d("SubscriptionRepository", "Response: ${response.body()?.subscriptions ?: emptyList()}")
                // If successful, return the list of subscriptions
                response.body()?.subscriptions ?: emptyList()
            }else{
                Log.e("SubscriptionRepository", "Failed to fetch subscriptions. Code: ${response.code()}")
                // If not successful, return a StatusResponse error
                emptyList()
            }

        }catch (e: Exception){
            //catch any exceptions
            Log.e("SubscriptionRepository", "Error fetching subscriptions", e)
            emptyList()
        }
    }

    // Function to remove a subscription from the list
    suspend fun deleteSubscription(subscriptionId: String): Boolean {
        return try {
            val response = subscriptionService.deleteSubscription(subscriptionId)
            if (response.isSuccessful) {
                Log.d("SubscriptionRepository", "Subscription deleted successfully")
                true
            } else {
                Log.e("SubscriptionRepository", "Failed to delete subscription. Code: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("SubscriptionRepository", "Error deleting subscription", e)
            false
        }
    }

    // Function to add a new subscription
    suspend fun addNewSubscription(mongoId: String, assignedCode: String): String?{
        try {
            val response = subscriptionService.addNewSubscription(NewSubscriptionRequest(mongoId, assignedCode))
            if (response.isSuccessful) {
                Log.d("SubscriptionRepository", "Subscription added successfully")
                return response.body()?.message // Return the server's message
            } else {
                Log.e("SubscriptionRepository", "Failed to add subscription. Code: ${response.code()}")
                return "Failed to add subscription." // Default failure message
            }
        } catch (e: Exception) {
            Log.e("SubscriptionRepository", "Error adding subscription", e)
            return "Error adding subscription." // Error message
        }
    }

    // Function to fetch my subscribers
    suspend fun fetchMySubscribers(userId: String): List<SubscriptionFetchResponse>? {
        return try{
            val response = subscriptionService.fetchMySubscribers(userId)
            if (response.isSuccessful) {
                Log.d("SubscriptionRepository", "Subscribers fetched successfully")
                Log.d("SubscriptionRepository", "Response: ${response.body()?: emptyList()}")
                // If successful, return the list of subscriptions
                response.body()?: emptyList()
            }else{
                Log.e("SubscriptionRepository", "Failed to fetch subscribers. Code: ${response.code()}")
                // If not successful, return a StatusResponse error
                emptyList()
            }

        }catch (e: Exception){
            //catch any exceptions
            Log.e("SubscriptionRepository", "Error fetching subscribers", e)
            emptyList()
        }
    }
}