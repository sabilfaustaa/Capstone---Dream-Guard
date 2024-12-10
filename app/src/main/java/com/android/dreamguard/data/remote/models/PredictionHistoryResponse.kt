package com.android.dreamguard.data.remote.models

import com.google.gson.annotations.SerializedName

data class PredictionHistoryResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<PredictionItem>
)

data class PredictionItem(
    @SerializedName("predictionResultId") val predictionResultId: Int,
    @SerializedName("predictionResultText") val predictionResultText: String,
    @SerializedName("createdAt") val createdAt: String
)
