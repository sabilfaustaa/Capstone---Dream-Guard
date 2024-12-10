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

    fun fetchPredictionHistory() {
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService(context).getPredictionHistory()
                if (response.isSuccessful) {
                    response.body()?.let {
                        _predictionHistory.postValue(it.data)
                    } ?: run {
                        _errorMessage.postValue("Failed to parse response.")
                    }
                } else {
                    _errorMessage.postValue("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error: ${e.message}")
            }
        }
    }
}
