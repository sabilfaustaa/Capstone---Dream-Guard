package com.android.dreamguard.data.remote.models

import com.google.gson.annotations.SerializedName

data class HomePageResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: HomePageData?
)

data class HomePageData(
    @SerializedName("profilePicture") val profilePicture: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("todaysDate") val todaysDate: String?,
    @SerializedName("lastPredictionCard") val lastPredictionCard: LastPredictionCard?,
    @SerializedName("avgSleepTimeCard") val avgSleepTimeCard: AvgSleepTimeCard?,
    @SerializedName("avgStressLevelCard") val avgStressLevelCard: AvgStressLevelCard?,
    @SerializedName("avgActivityLevelCard") val avgActivityLevelCard: AvgActivityLevelCard?,
    @SerializedName("avgSleepQualityCard") val avgSleepQualityCard: AvgSleepQualityCard?
)

data class LastPredictionCard(
    @SerializedName("id") val id: Int,
    @SerializedName("predictionResultText") val predictionResultText: String,
    @SerializedName("predictionResultId") val predictionResultId: String,
    @SerializedName("createdAt") val createdAt: String
)

data class AvgSleepTimeCard(
    @SerializedName("avgSleepTime") val avgSleepTime: String,
    @SerializedName("sleepGoal") val sleepGoal: String,
    @SerializedName("difference") val difference: String
)

data class AvgStressLevelCard(
    @SerializedName("avgStressLevel") val avgStressLevel: Int,
    @SerializedName("expression") val expression: String
)

data class AvgActivityLevelCard(
    @SerializedName("avgActivityLevel") val avgActivityLevel: Int,
    @SerializedName("expression") val expression: String
)

data class AvgSleepQualityCard(
    @SerializedName("avgSleepQuality") val avgSleepQuality: Int,
    @SerializedName("expression") val expression: String
)
