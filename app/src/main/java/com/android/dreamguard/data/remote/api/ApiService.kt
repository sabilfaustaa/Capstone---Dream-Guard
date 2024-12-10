package com.android.dreamguard.data.remote.api

import com.android.dreamguard.data.remote.models.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("/api/user/profile")
    suspend fun getUserProfile(): Response<RegisterResponse>

    @GET("/api/user/homepage")
    suspend fun getHomePageData(): Response<HomePageResponse>

    @POST("api/user/register")
    suspend fun registerNewUser(
        @Body requestBody: Map<String, String>
    ): Response<RegisterResponse>

    @POST("api/user/predictions")
    suspend fun addNewPrediction(
        @Body predictionData: Map<String, Int>
    ): Response<PredictionResponse>

    @GET("api/user/predictions")
    suspend fun getAllPredictions(): Response<PredictionHistoryResponse>

    @GET("api/user/predictions/filter")
    suspend fun getFilteredPredictions(
        @Query("predictionResult") predictionResult: String
    ): Response<PredictionHistoryResponse>

    @POST("/api/user/sleep-schedules")
    suspend fun addSleepSchedule(
        @Body schedule: SleepScheduleRequest
    ): Response<SleepScheduleResponse>

    @GET("/api/user/sleep-schedules")
    suspend fun getSleepSchedules(): Response<SleepScheduleListResponse>

    @PATCH("/api/user/sleep-schedules/{id}")
    suspend fun updateSleepSchedule(
        @Path("id") scheduleId: String,
        @Body schedule: SleepScheduleRequest
    ): Response<SleepScheduleResponse>

    @PATCH("/api/user/sleep-goals")
    suspend fun updateSleepGoals(
        @Body goals: Map<String, Int>
    ): Response<Map<String, String>>

    @GET("/api/user/sleep-goals")
    suspend fun getSleepGoals(): Response<Map<String, String>>
}
