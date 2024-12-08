package com.android.dreamguard.data.remote.models

import com.google.gson.annotations.SerializedName

data class LatestModelResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("model_url") val modelUrl: String?,
    @SerializedName("version") val version: String?
)
