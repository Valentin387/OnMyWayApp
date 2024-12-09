package com.sindesoft.onmywayapp.io

import android.content.Context
import com.sindesoft.onmywayapp.BuildConfig
import com.sindesoft.onmywayapp.data.DTO.SignInRequest
import com.sindesoft.onmywayapp.data.DTO.SignInResponse
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthService {

    @POST("signin")
    suspend fun postLogin(
        @Body loginRequest: SignInRequest
    ): retrofit2.Response<SignInResponse>


    companion object Factory {
        private const val BASE_URL = BuildConfig.BASE_URL

        fun create(context: Context): AuthService {


            val client = OkHttpClient.Builder().build()
            return retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AuthService::class.java)
        }
    }


}