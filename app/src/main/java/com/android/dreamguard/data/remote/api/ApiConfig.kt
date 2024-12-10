package com.android.dreamguard.data.remote.api

import android.content.Context
import android.content.Intent
import com.android.dreamguard.data.local.UserPreferences
import com.android.dreamguard.ui.auth.LoginActivity
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    private const val BASE_URL = "https://dreamguard-api-257341063998.asia-southeast2.run.app/"

    fun getApiService(context: Context): ApiService {
        val userPreferences = UserPreferences(context)

        val authInterceptor = Interceptor { chain ->
            var request = chain.request()

            val token = userPreferences.getToken()
            if (!token.isNullOrEmpty()) {
                request = request.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            }

            val response = chain.proceed(request)

            if (response.code == 401 || response.code == 403) {
                synchronized(this) {
                    userPreferences.clear()
                    val intent = Intent(context, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                }
            }

            response
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
