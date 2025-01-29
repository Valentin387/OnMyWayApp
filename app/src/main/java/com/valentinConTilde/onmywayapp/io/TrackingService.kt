package com.valentinConTilde.onmywayapp.io

import android.content.Context
import com.valentinConTilde.onmywayapp.BuildConfig
import com.valentinConTilde.onmywayapp.data.DTO.StatusResponse
import com.valentinConTilde.onmywayapp.data.DTO.UserLocationInMap
import com.valentinConTilde.onmywayapp.data.models.UserLocation
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TrackingService {

    @POST("new_userLocation")
    suspend fun uploadNewLocation(
        @Body userLocation: UserLocation
    ): retrofit2.Response<StatusResponse>

    @GET("my_subscriptions_latest_locations")
    suspend fun fetchMyChannelsLatestLatLong(
        @Query("userId") userId: String
    ): retrofit2.Response<List<UserLocationInMap>>

    @GET("user_location_history")
    suspend fun fetchUserHistory(
        @Query("userId") userId: String,
        @Query("startDate") startDateMillis : String,
        @Query("endDate") endDateMillis : String
    ): retrofit2.Response<List<UserLocationInMap>>

    companion object Factory {
        private const val BASE_URL = BuildConfig.BASE_URL + "tracking/"

        fun create(context: Context): TrackingService {

            val client = OkHttpClient.Builder().build()
            return retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TrackingService::class.java)
        }
    }
}
