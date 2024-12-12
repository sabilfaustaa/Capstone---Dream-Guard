package com.android.dreamguard.ui.settings

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import com.capstone.dreamguard.R
import com.android.dreamguard.data.local.UserPreferences
import com.android.dreamguard.data.remote.api.ApiConfig
import com.android.dreamguard.ui.auth.LoginActivity
import com.android.dreamguard.ui.changepassword.ChangePasswordActivity
import com.android.dreamguard.ui.feedback.FeedbackActivity
import com.android.dreamguard.ui.home.HomeActivity
import com.android.dreamguard.ui.prediction.ActivityPrediction
import com.android.dreamguard.ui.profile.EditProfileActivity
import com.android.dreamguard.utils.CustomBottomNavigation
import com.bumptech.glide.Glide
import com.capstone.dreamguard.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ActivitySettings : ComponentActivity() {

    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userPreferences = UserPreferences(this)

        setContent {
            SettingsScreen()
        }
    }

    @Composable
    fun SettingsScreen() {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AndroidView(
                factory = { context ->
                    val binding = ActivitySettingsBinding.inflate(LayoutInflater.from(context))

                    loadUserProfile(binding)

                    binding.logoutButton.setOnClickListener { logout() }
                    binding.changePasswordRow.setOnClickListener { navigateToChangePassword() }
                    binding.deleteAccountRow.setOnClickListener { navigateToDeleteAccount() }
                    binding.giveFeedbackRow.setOnClickListener { navigateToGiveFeedback() }
                    binding.editProfileIcon.setOnClickListener { navigateToEditProfile() }

                    binding.root
                },
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                CustomBottomNavigation(
                    currentTab = "Profile",
                    onTabSelected = { selectedTab ->
                        when (selectedTab) {
                            "Home" -> navigateToHome()
                            "Predict" -> navigateToPredict()
                            "Profile" -> {}
                        }
                    }
                )
            }
        }
    }

    private fun loadUserProfile(binding: ActivitySettingsBinding) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        currentUser?.let { user ->
            val name = user.displayName ?: "User"
            val email = user.email ?: "No email available"
            val photoUrl = user.photoUrl

            binding.profileName.text = name
            binding.profileEmail.text = email

            Glide.with(this)
                .load(photoUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(binding.profileImage)
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        userPreferences.clear()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun navigateToPredict() {
        val intent = Intent(this, ActivityPrediction::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun navigateToChangePassword() {
        val intent = Intent(this, ChangePasswordActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun navigateToDeleteAccount() {
        showDeleteAccountPopup()
    }

    private fun navigateToGiveFeedback() {
        val intent = Intent(this, FeedbackActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun navigateToEditProfile() {
        val intent = Intent(this, EditProfileActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun showDeleteAccountPopup() {
        val dialogView = layoutInflater.inflate(R.layout.msg_delete_account, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val yesButton = dialogView.findViewById<View>(R.id.dismissButton)
        val noButton = dialogView.findViewById<View>(R.id.dismissNoButton)

        yesButton.setOnClickListener {
            deleteAccount(dialog)
        }

        noButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteAccount(dialog: Dialog) {
        lifecycleScope.launch {
            try {
                val apiService = ApiConfig.getApiService(this@ActivitySettings)
                val token = userPreferences.getToken()

                if (token.isNullOrEmpty()) {
                    showErrorSnackbar("Authentication failed. Please log in again.")
                    return@launch
                }

                val response = apiService.deleteUserAccount("Bearer $token")
                if (response.isSuccessful) {
                    logout()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error occurred"
                    showErrorSnackbar(errorMessage)
                }
            } catch (e: Exception) {
                showErrorSnackbar(e.localizedMessage ?: "Failed to delete account")
            } finally {
                dialog.dismiss()
            }
        }
    }

    private fun showErrorSnackbar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }
}
