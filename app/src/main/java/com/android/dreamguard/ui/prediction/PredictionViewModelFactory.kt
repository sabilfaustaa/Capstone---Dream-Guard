package com.android.dreamguard.ui.prediction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.dreamguard.data.repository.PredictionRepository

class PredictionViewModelFactory(private val repository: PredictionRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PredictionViewModel::class.java)) {
            return PredictionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
