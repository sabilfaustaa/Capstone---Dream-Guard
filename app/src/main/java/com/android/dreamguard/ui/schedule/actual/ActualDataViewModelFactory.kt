package com.android.dreamguard.ui.schedule.actual

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ActualDataViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActualDataViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActualDataViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
