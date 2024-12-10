package com.android.dreamguard.ui.schedule.add

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.dreamguard.data.remote.api.ApiConfig
import com.android.dreamguard.data.remote.models.SleepScheduleRequest
import kotlinx.coroutines.launch

class AddScheduleViewModel(private val context: Context) : ViewModel() {

    private val _addScheduleResult = MutableLiveData<Boolean>()
    val addScheduleResult: LiveData<Boolean> = _addScheduleResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun addSleepSchedule(bedTime: String, wakeUpTime: String, wakeUpAlarm: Boolean, sleepReminders: Boolean) {
        val apiService = ApiConfig.getApiService(context)

        val request = SleepScheduleRequest(
            bedTime = bedTime,
            wakeUpTime = wakeUpTime,
            wakeUpAlarm = wakeUpAlarm,
            sleepReminders = sleepReminders
        )

        viewModelScope.launch {
            try {
                val response = apiService.addSleepSchedule(request)
                if (response.isSuccessful) {
                    _addScheduleResult.value = true
                } else {
                    _addScheduleResult.value = false
                    _errorMessage.value = "Failed to add schedule: ${response.message()}"
                }
            } catch (e: Exception) {
                _addScheduleResult.value = false
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }


}
