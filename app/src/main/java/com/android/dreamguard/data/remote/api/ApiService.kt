package com.android.dreamguard.data.remote.api

import com.android.dreamguard.data.remote.models.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/user/register")
    suspend fun registerNewUser(
        @Header("Authorization") authorization: String,
        @Body requestBody: Map<String, String>
    ): Response<RegisterResponse>

//    @GET("api/user/profile")
//    suspend fun getUserProfile(
//        @Header("Authorization") token: String
//    ): Response<UserProfileResponse>
//
//    @PATCH("api/user/profile")
//    @Multipart
//    suspend fun updateUserProfile(
//        @Header("Authorization") token: String,
//        @PartMap profileData: Map<String, String>,
//        @Part profilePicture: MultipartBody.Part?
//    ): Response<UpdateProfileResponse>
//
//    @POST("api/user/feedback")
//    suspend fun addFeedback(
//        @Header("Authorization") token: String,
//        @Body feedback: FeedbackRequest
//    ): Response<FeedbackResponse>
//
//    @POST("api/user/predictions")
//    suspend fun addPrediction(
//        @Header("Authorization") token: String,
//        @Body predictionRequest: PredictionRequest
//    ): Response<PredictionResponse>
//
//    @GET("api/user/predictions")
//    suspend fun getAllPredictions(
//        @Header("Authorization") token: String
//    ): Response<List<PredictionResponse>>
//
//    @GET("api/user/predictions/latest")
//    suspend fun getLatestPrediction(
//        @Header("Authorization") token: String
//    ): Response<LatestPredictionResponse>
//
//    @GET("api/user/predictions/filter")
//    suspend fun filterPredictions(
//        @Header("Authorization") token: String,
//        @Query("predictionResultId") resultId: Int
//    ): Response<List<PredictionResponse>>
//
//    @POST("api/user/sleep-schedules")
//    suspend fun addSleepSchedule(
//        @Header("Authorization") token: String,
//        @Body sleepScheduleRequest: SleepScheduleRequest
//    ): Response<SleepScheduleResponse>
//
//    @GET("api/user/sleep-schedules")
//    suspend fun getAllSleepSchedules(
//        @Header("Authorization") token: String
//    ): Response<List<SleepScheduleResponse>>
//
//    @PATCH("api/user/sleep-schedules/{id}")
//    suspend fun updateSleepSchedule(
//        @Header("Authorization") token: String,
//        @Path("id") id: String,
//        @Body sleepScheduleUpdateRequest: SleepScheduleUpdateRequest
//    ): Response<SleepScheduleResponse>
//
//    @PATCH("api/user/sleep-goals")
//    suspend fun addOrUpdateSleepGoals(
//        @Header("Authorization") token: String,
//        @Body sleepGoalsRequest: SleepGoalsRequest
//    ): Response<SleepGoalsResponse>
//
//    @GET("api/user/sleep-goals")
//    suspend fun getSleepGoals(
//        @Header("Authorization") token: String
//    ): Response<SleepGoalsResponse>
//
//    @GET("api/user/homepage")
//    suspend fun getHomepageData(
//        @Header("Authorization") token: String
//    ): Response<HomepageResponse>
//
    @GET("api/models/latest")
    suspend fun fetchLatestModel(): Response<LatestModelResponse>
}
