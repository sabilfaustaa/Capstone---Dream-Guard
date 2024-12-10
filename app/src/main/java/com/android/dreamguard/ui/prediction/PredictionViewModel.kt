package com.android.dreamguard.ui.prediction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.dreamguard.data.remote.models.PredictionResponse
import com.android.dreamguard.data.repository.PredictionRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class PredictionViewModel(private val repository: PredictionRepository) : ViewModel() {

    private val _predictionResult = MutableLiveData<Response<PredictionResponse>>()
    val predictionResult: LiveData<Response<PredictionResponse>> = _predictionResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun submitPrediction(predictionData: Map<String, Int>) {
        viewModelScope.launch {
            try {
                val response = repository.submitPrediction(predictionData)
                _predictionResult.postValue(response)
            } catch (e: Exception) {
                _error.postValue(e.message ?: "An unknown error occurred")
            }
        }
    }
}
