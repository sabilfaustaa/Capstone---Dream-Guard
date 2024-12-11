package com.android.dreamguard.ui.schedule.list

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.dreamguard.data.remote.api.ApiConfig
import com.android.dreamguard.data.remote.models.SleepSchedule
import kotlinx.coroutines.launch

class SleepScheduleViewModel(private val context: Context) : ViewModel() {
    private val _sleepGoal = MutableLiveData<String>()
    val sleepGoal: LiveData<String> get() = _sleepGoal

    private val _sleepSchedules = MutableLiveData<List<SleepSchedule>>()
    val sleepSchedules: LiveData<List<SleepSchedule>> = _sleepSchedules

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> get() = _loadingState

    fun fetchSleepSchedules() {
        _loadingState.value = true
        val apiService = ApiConfig.getApiService(context)

        viewModelScope.launch {
            try {
                val response = apiService.getSleepSchedules()
                if (response.isSuccessful) {
                    val schedules = response.body()?.data ?: emptyList()
                    _sleepSchedules.value = schedules
                } else {
                    _errorMessage.value = "Failed to fetch schedules: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _loadingState.value = false
            }
        }
    }

    fun fetchSleepGoal() {
        _loadingState.value = true
        val apiService = ApiConfig.getApiService(context)

        viewModelScope.launch {
            try {
                val response = apiService.getSleepGoals()
                if (response.isSuccessful) {
                    _sleepGoal.value = response.body()?.data?.sleepGoal ?: "N/A"
                } else {
                    _errorMessage.value = "Failed to fetch sleep goal: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            } finally {
                _loadingState.value = false
            }
        }
    }
}
