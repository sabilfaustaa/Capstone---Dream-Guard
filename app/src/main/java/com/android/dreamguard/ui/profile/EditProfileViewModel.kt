package com.android.dreamguard.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.dreamguard.data.local.UserPreferences
import com.android.dreamguard.data.remote.api.ApiConfig
import com.android.dreamguard.data.remote.models.UserProfile
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class EditProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?> get() = _userProfile

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> get() = _updateSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun loadUserProfile() {
        val profile = userPreferences.getUserProfile()
        if (profile != null) {
            _userProfile.value = profile
        } else {
            _errorMessage.value = "User profile not found in preferences."
        }
    }

    private fun getMimeType(file: File): String {
        val extension = file.extension.lowercase()
        return when (extension) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            else -> "application/octet-stream"
        }
    }

    fun updateUserProfile(
        name: String?,
        email: String?,
        age: Int?,
        gender: String?,
        occupation: String?,
        profilePicture: File?
    ) {
        val apiService = ApiConfig.getApiService(getApplication())
        val token = userPreferences.getToken()

        if (token == null) {
            _errorMessage.value = "User not authenticated."
            return
        }

        val requestBody = mutableMapOf<String, RequestBody>().apply {
            name?.let { put("name", it.toRequestBody("text/plain".toMediaTypeOrNull())) }
            email?.let { put("email", it.toRequestBody("text/plain".toMediaTypeOrNull())) }
            age?.let { put("age", it.toString().toRequestBody("text/plain".toMediaTypeOrNull())) }
            gender?.let { put("gender", it.toRequestBody("text/plain".toMediaTypeOrNull())) }
            occupation?.let { put("occupation", it.toRequestBody("text/plain".toMediaTypeOrNull())) }
        }

        val profilePicturePart = profilePicture?.let {
            val mimeType = getMimeType(it)
            MultipartBody.Part.createFormData(
                "profilePicture",
                it.name,
                it.asRequestBody(mimeType.toMediaTypeOrNull())
            )
        }

        viewModelScope.launch {
            try {
                val response = apiService.updateUserProfile(
                    body = requestBody,
                    profilePicture = profilePicturePart
                )
                if (response.isSuccessful) {
                    val updatedProfile = response.body()?.data
                    if (updatedProfile != null) {
                        userPreferences.saveUserProfile(updatedProfile)
                        _userProfile.value = updatedProfile
                        _updateSuccess.value = true
                    } else {
                        _errorMessage.value = "Failed to update profile: No data received."
                    }
                } else {
                    _errorMessage.value = response.errorBody()?.string() ?: "Unknown error occurred."
                }

            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            }
        }
    }
}
