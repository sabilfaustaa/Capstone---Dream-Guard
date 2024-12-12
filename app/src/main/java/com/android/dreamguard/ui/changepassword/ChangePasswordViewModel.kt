package com.android.dreamguard.ui.changepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.dreamguard.data.repository.AuthRepository
import kotlinx.coroutines.launch

class ChangePasswordViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun changePassword(currentPassword: String, newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val isChanged = authRepository.changePassword(currentPassword, newPassword)
                if (isChanged) {
                    onSuccess()
                } else {
                    onError("Password change failed.")
                }
            } catch (e: Exception) {
                onError(e.message ?: "An error occurred.")
            }
        }
    }
}
