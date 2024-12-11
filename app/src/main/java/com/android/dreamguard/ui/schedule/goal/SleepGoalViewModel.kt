package com.android.dreamguard.ui.schedule.goal

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.dreamguard.data.remote.api.ApiConfig
import kotlinx.coroutines.launch

class SleepGoalViewModel(private val context: Context) : ViewModel() {

    private val _sleepGoal = MutableLiveData<String>()
    val sleepGoal: LiveData<String> get() = _sleepGoal

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> get() = _updateSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchSleepGoal() {
        val apiService = ApiConfig.getApiService(context)
        viewModelScope.launch {
            try {
                val response = apiService.getSleepGoals()
                if (response.isSuccessful) {
                    _sleepGoal.value = response.body()?.data?.sleepGoal ?: "08:00"
                } else {
                    _errorMessage.value = "Failed to fetch sleep goal: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    fun updateSleepGoal(newSleepGoal: String) {
        val apiService = ApiConfig.getApiService(context)
        viewModelScope.launch {
            try {
                val response = apiService.updateSleepGoals(mapOf("sleepGoal" to newSleepGoal.toInt()))
                if (response.isSuccessful) {
                    _updateSuccess.value = true
                } else {
                    _updateSuccess.value = false
                    _errorMessage.value = "Failed to update sleep goal: ${response.message()}"
                }
            } catch (e: Exception) {
                _updateSuccess.value = false
                _errorMessage.value = "Error: ${e.localizedMessage}"
            }
        }
    }
}
