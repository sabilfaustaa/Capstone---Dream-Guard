package com.android.dreamguard.ui.schedule.goal

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.dreamguard.data.remote.api.ApiConfig
import kotlinx.coroutines.launch

class SleepGoalViewModel(private val context: Context) : ViewModel() {

    private val _sleepGoal = MutableLiveData<Pair<Int, Int>>() // Hours and Minutes
    val sleepGoal: LiveData<Pair<Int, Int>> get() = _sleepGoal

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
                    val sleepGoal = response.body()?.sleepGoal?.split(" ") // e.g., "8h 30m"
                    val hours = sleepGoal?.getOrNull(0)?.replace("h", "")?.toIntOrNull() ?: 8
                    val minutes = sleepGoal?.getOrNull(1)?.replace("m", "")?.toIntOrNull() ?: 0
                    _sleepGoal.value = Pair(hours, minutes)
                } else {
                    _errorMessage.value = "Failed to fetch sleep goal: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    fun updateSleepGoal(hours: Int, minutes: Int) {
        val apiService = ApiConfig.getApiService(context)
        viewModelScope.launch {
            try {
                val requestBody = mapOf("hours" to hours, "minutes" to minutes)
                val response = apiService.updateSleepGoals(requestBody)
                if (response.isSuccessful) {
                    _updateSuccess.value = true
                    _sleepGoal.value = Pair(hours, minutes)
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
