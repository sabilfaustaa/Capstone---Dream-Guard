package com.android.dreamguard.data.remote.models

data class PredictionResponse(
    val status: String,
    val message: String,
    val data: PredictionData
)

data class PredictionData(
    val prediction: PredictionResult
)

data class PredictionResult(
    val id: Int,
    val result: String,
    val confidencePercentage: Map<String, Float>
)
