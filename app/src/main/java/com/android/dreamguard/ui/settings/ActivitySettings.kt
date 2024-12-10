package com.android.dreamguard.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.android.dreamguard.data.local.UserPreferences
import com.android.dreamguard.ui.auth.LoginActivity
import com.android.dreamguard.ui.home.HomeActivity
import com.android.dreamguard.ui.prediction.ActivityPrediction
import com.android.dreamguard.utils.CustomBottomNavigation
import com.capstone.dreamguard.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth

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
        // startActivity(Intent(this, ChangePasswordActivity::class.java))
    }

    private fun navigateToDeleteAccount() {
        // startActivity(Intent(this, DeleteAccountActivity::class.java))
    }

    private fun navigateToGiveFeedback() {
        // startActivity(Intent(this, GiveFeedbackActivity::class.java))
    }

    private fun navigateToEditProfile() {
        // startActivity(Intent(this, EditProfileActivity::class.java))
    }
}
