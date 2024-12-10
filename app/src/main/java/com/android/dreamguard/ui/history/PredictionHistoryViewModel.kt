package com.android.dreamguard.ui.history

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.dreamguard.data.remote.api.ApiConfig
import com.android.dreamguard.data.remote.models.PredictionItem
import kotlinx.coroutines.launch

class PredictionHistoryViewModel(private val context: Context) : ViewModel() {

    private val _predictionHistory = MutableLiveData<List<PredictionItem>>()
    val predictionHistory: LiveData<List<PredictionItem>> = _predictionHistory

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchPredictionHistory(filter: String? = null) {
        val apiService = ApiConfig.getApiService(context)

        viewModelScope.launch {
            try {
                val response = if (filter.isNullOrEmpty()) {
                    apiService.getAllPredictions() // Fetch all predictions
                } else {
                    apiService.getFilteredPredictions(filter) // Fetch filtered predictions
                }
                if (response.isSuccessful) {
                    _predictionHistory.value = response.body()?.data ?: emptyList()
                } else {
                    _errorMessage.value = "Failed to fetch predictions: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }
}
