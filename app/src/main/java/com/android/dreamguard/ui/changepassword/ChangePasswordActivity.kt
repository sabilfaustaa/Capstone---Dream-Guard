package com.android.dreamguard.ui.changepassword

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.dreamguard.data.remote.api.ApiConfig
import com.android.dreamguard.data.repository.AuthRepository
import com.android.dreamguard.ui.home.HomeActivity
import com.android.dreamguard.ui.settings.ActivitySettings
import com.capstone.dreamguard.databinding.ActivityChangePasswordBinding
import com.capstone.dreamguard.R
import com.google.android.material.snackbar.Snackbar

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private val authRepository by lazy { AuthRepository(ApiConfig.getApiService(this)) }
    private val viewModel: ChangePasswordViewModel by viewModels {
        ChangePasswordViewModelFactory(authRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            navigateToHome()
        }

        binding.changePasswordButon.setOnClickListener {
            val currentPassword = binding.currentEditText.text.toString()
            val newPassword = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()

            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showSnackbar(getString(R.string.error_empty_fields))
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                showSnackbar(getString(R.string.error_password_mismatch))
                return@setOnClickListener
            }

            viewModel.changePassword(
                currentPassword = currentPassword,
                newPassword = newPassword,
                onSuccess = { showSnackbar(getString(R.string.success_change_password)) },
                onError = { showSnackbar(it) }
            )
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun navigateToHome() {
        val intent = Intent(this, ActivitySettings::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
