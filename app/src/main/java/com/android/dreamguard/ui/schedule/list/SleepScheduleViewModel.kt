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

    private val _sleepSchedules = MutableLiveData<List<SleepSchedule>>()
    val sleepSchedules: LiveData<List<SleepSchedule>> = _sleepSchedules

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchSleepSchedules() {
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
            }
        }

    }
}
