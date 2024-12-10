package com.android.dreamguard.data.remote.models

data class PredictionResponse(
    val status: String,
    val message: String,
    val data: PredictionData
)

data class PredictionData(
    val gender: Int,
    val age: Int,
    val sleepDuration: Int,
    val sleepQuality: Int,
    val occupation: Int,
    val activityLevel: Int,
    val stressLevel: Int,
    val weight: Int,
    val height: Int,
    val heartRate: Int,
    val dailySteps: Int,
    val systolic: Int,
    val diastolic: Int,
    val bmiCategory: Int,
    val prediction: PredictionDetail
)

data class PredictionDetail(
    val id: Int,
    val result: String,
    val confidencePercentage: Map<String, String>
)
