package com.valentinConTilde.onmywayapp.io

import android.content.Context
import com.valentinConTilde.onmywayapp.BuildConfig
import com.valentinConTilde.onmywayapp.data.DTO.StatusResponse
import com.valentinConTilde.onmywayapp.data.models.UserLocation
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface UserLocationBroadcastService {

    @POST("new_userLocation")
    suspend fun uploadNewLocation(
        @Body userLocation: UserLocation
    ): retrofit2.Response<StatusResponse>

    companion object Factory {
        private const val BASE_URL = BuildConfig.BASE_URL + "socket/"

        fun create(context: Context): UserLocationBroadcastService {

            val client = OkHttpClient.Builder().build()
            return retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserLocationBroadcastService::class.java)
        }
    }
}