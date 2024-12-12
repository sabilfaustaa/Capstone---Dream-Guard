package com.android.dreamguard.ui.feedback

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.capstone.dreamguard.R
import com.android.dreamguard.data.remote.api.ApiConfig
import com.android.dreamguard.data.repository.FeedbackRepository
import com.android.dreamguard.ui.home.HomeActivity
import com.android.dreamguard.ui.settings.ActivitySettings
import com.capstone.dreamguard.databinding.ActivityFeedbackBinding
import com.google.android.material.snackbar.Snackbar

class FeedbackActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedbackBinding
    private val feedbackViewModel: FeedbackViewModel by viewModels {
        FeedbackViewModelFactory(FeedbackRepository(ApiConfig.getApiService(this)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            navigateToHome()
        }

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.feedbackButton.setOnClickListener {
            val feedback = binding.currentEditText.text.toString().trim()
            if (feedback.isEmpty()) {
                showSnackbar(getString(R.string.error_feedback_empty_fields))
                return@setOnClickListener
            }

            showLoading(true)
            feedbackViewModel.addFeedback(feedback)
        }
    }

    private fun observeViewModel() {
        feedbackViewModel.feedbackState.observe(this) { state ->
            showLoading(false)
            state?.let {
                if (it.isSuccess) {
                    showSnackbar(getString(R.string.success_feedback_added))
                    binding.currentEditText.text?.clear()
                } else {
                    showSnackbar(it.errorMessage ?: getString(R.string.error_feedback_failed))
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.feedbackButton.isEnabled = !isLoading
        binding.feedbackButton.text = if (isLoading) "" else getString(R.string.button_feed)
        binding.feedbackButton.isClickable = !isLoading
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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
