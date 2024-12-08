package com.android.dreamguard.ui.prediction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.dreamguard.data.repository.ModelRepository

class PredictionViewModelFactory(
    private val repository: ModelRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PredictionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PredictionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
