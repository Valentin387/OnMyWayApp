package com.valentinConTilde.onmywayapp.data.models

import com.google.gson.annotations.SerializedName

data class Subscription(
    @SerializedName("_id")
    val id: String? = "",
    val userId: String? = "", //Your mongo id
    val channelId: String? = "", //The id of the person you are subscribed to, in this project, the people are the channels themselves
    val timestamp: String = System.currentTimeMillis().toString(), //timestamp in milliseconds
    val currentNotificationStatus: SubscriptionNotificationStatus = SubscriptionNotificationStatus.UNKNOWN
)
