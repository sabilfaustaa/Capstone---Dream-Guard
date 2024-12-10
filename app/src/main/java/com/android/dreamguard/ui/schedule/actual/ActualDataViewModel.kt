package com.android.dreamguard.ui.schedule.actual

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.dreamguard.data.remote.api.ApiConfig
import com.android.dreamguard.data.remote.models.SleepScheduleRequest
import kotlinx.coroutines.launch

class ActualDataViewModel(private val context: Context) : ViewModel() {

    private val _updateResult = MutableLiveData<Boolean>()
    val updateResult: LiveData<Boolean> = _updateResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun updateSleepSchedule(scheduleId: String, actualBedTime: String, actualWakeUpTime: String, sleepQuality: String?, notes: String) {
        val apiService = ApiConfig.getApiService(context)

        val request = SleepScheduleRequest(
            id = scheduleId,
            actualBedTime = actualBedTime,
            actualWakeUpTime = actualWakeUpTime,
            sleepQuality = sleepQuality,
            notes = notes
        )

        viewModelScope.launch {
            try {
                val response = apiService.updateSleepSchedule(scheduleId, request)
                if (response.isSuccessful) {
                    _updateResult.postValue(true)
                } else {
                    _updateResult.postValue(false)
                    _errorMessage.postValue("Failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _updateResult.postValue(false)
                _errorMessage.postValue(e.message)
            }
        }
    }
}
