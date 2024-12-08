package com.android.dreamguard.ui.prediction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.dreamguard.data.remote.models.LatestModelResponse
import com.android.dreamguard.data.repository.ModelRepository
import kotlinx.coroutines.launch

class PredictionViewModel(private val repository: ModelRepository) : ViewModel() {

    private val _modelResponse = MutableLiveData<LatestModelResponse>()
    val modelResponse: LiveData<LatestModelResponse> get() = _modelResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchLatestModelUrl() {
        viewModelScope.launch {
            try {
                val response = repository.getLatestModelUrl()
                if (response.isSuccessful) {
                    _modelResponse.postValue(response.body())
                } else {
                    _error.postValue("Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }
}
