package com.android.dreamguard.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.dreamguard.data.local.UserPreferences
import com.android.dreamguard.data.remote.api.ApiConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userPreferences = UserPreferences(application)

    private val _authState = MutableLiveData<Boolean>()
    val authState: LiveData<Boolean> get() = _authState

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private fun saveTokenToPreferences(token: String) {
        userPreferences.saveToken(token)
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = firebaseAuth.currentUser
                            user?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                                if (tokenTask.isSuccessful) {
                                    val token = tokenTask.result?.token
                                    if (token != null) {
                                        saveTokenToPreferences(token)
                                        fetchUserProfile()
                                    }
                                } else {
                                    _errorMessage.value = tokenTask.exception?.message
                                }
                            }
                        } else {
                            _errorMessage.value = task.exception?.message
                        }
                    }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            }
        }
    }

    fun googleSignIn(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        viewModelScope.launch {
            try {
                val authResult = withContext(Dispatchers.IO) {
                    firebaseAuth.signInWithCredential(credential).await()
                }

                val token = authResult.user?.getIdToken(true)?.await()?.token
                    ?: throw Exception("Failed to retrieve token")

                saveTokenToPreferences(token)
                fetchUserProfile()
                _authState.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Google Sign-In failed"
            }
        }
    }


    fun fetchUserProfile() {
        viewModelScope.launch {
            val apiService = ApiConfig.getApiService(getApplication())
            val response = apiService.getUserProfile()
            if (response.isSuccessful) {
                val userProfile = response.body()?.data
                userProfile?.let {
                    userPreferences.saveUserProfile(it)
                    _authState.value = true
                }
            } else {
                _errorMessage.value = "Failed to fetch user profile"
            }
        }

    }
}
