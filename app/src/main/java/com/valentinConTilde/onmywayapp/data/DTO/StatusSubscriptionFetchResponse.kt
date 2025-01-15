package com.valentinConTilde.onmywayapp.data.DTO

data class StatusSubscriptionFetchResponse(
    val status: String,
    val subscriptions: List<SubscriptionFetchResponse>
)
