package com.android.dreamguard.ui.feedback

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.dreamguard.data.repository.FeedbackRepository
import kotlinx.coroutines.launch

class FeedbackViewModel(private val repository: FeedbackRepository) : ViewModel() {

    private val _feedbackState = MutableLiveData<FeedbackState?>()
    val feedbackState: LiveData<FeedbackState?> get() = _feedbackState

    fun addFeedback(feedback: String) {
        viewModelScope.launch {
            try {
                val response = repository.addFeedback(feedback)
                if (response.isSuccessful) {
                    _feedbackState.value = FeedbackState(isSuccess = true)
                } else {
                    _feedbackState.value = FeedbackState(
                        isSuccess = false,
                        errorMessage = response.message()
                    )
                }
            } catch (e: Exception) {
                _feedbackState.value = FeedbackState(
                    isSuccess = false,
                    errorMessage = e.message
                )
            }
        }
    }
}

data class FeedbackState(
    val isSuccess: Boolean,
    val errorMessage: String? = null
)
