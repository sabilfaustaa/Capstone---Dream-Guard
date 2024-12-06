package com.android.dreamguard.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.dreamguard.data.local.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

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
                                        _authState.value = true
                                        saveTokenToPreferences(token)
                                    }
                                } else {
                                    _errorMessage.value = tokenTask.exception?.message
                                }
                            }
                        } else {
                            _authState.value = false
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
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = true
                } else {
                    _errorMessage.value = task.exception?.message ?: "Google Sign-In failed"
                }
            }
    }
}
