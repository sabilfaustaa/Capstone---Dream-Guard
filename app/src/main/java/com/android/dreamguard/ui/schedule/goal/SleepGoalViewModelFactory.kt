package com.android.dreamguard.ui.schedule.goal

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SleepGoalViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SleepGoalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SleepGoalViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
