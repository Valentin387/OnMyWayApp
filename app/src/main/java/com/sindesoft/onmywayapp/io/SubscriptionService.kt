package com.sindesoft.onmywayapp.io

import android.content.Context
import com.sindesoft.onmywayapp.BuildConfig
import com.sindesoft.onmywayapp.data.DTO.NewSubscriptionRequest
import com.sindesoft.onmywayapp.data.DTO.SignInRequest
import com.sindesoft.onmywayapp.data.DTO.SignInResponse
import com.sindesoft.onmywayapp.data.DTO.StatusResponse
import com.sindesoft.onmywayapp.data.DTO.StatusSubscriptionFetchResponse
import com.sindesoft.onmywayapp.data.DTO.SubscriptionFetchResponse
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SubscriptionService {

    @POST("new_subscription")
    suspend fun addNewSubscription(
        @Body subscriptionRequest: NewSubscriptionRequest
    ): retrofit2.Response<StatusResponse>

    @GET("channels")
    suspend fun fetchMySubscriptions(
        @Query("userId") userId: String
    ): retrofit2.Response<StatusSubscriptionFetchResponse>

    @GET("fetch_subscribers/{id}")
    suspend fun fetchMySubscribers(
        @Path("id") channelId: String
    ): retrofit2.Response<StatusSubscriptionFetchResponse>

    @DELETE("delete/{id}")
    suspend fun deleteSubscription(
        @Path("id") subscriptionId: String
    ): retrofit2.Response<StatusResponse>


    companion object Factory {
        private const val BASE_URL = BuildConfig.BASE_URL + "subscription/"

        fun create(context: Context): SubscriptionService {


            val client = OkHttpClient.Builder().build()
            return retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SubscriptionService::class.java)
        }
    }

}