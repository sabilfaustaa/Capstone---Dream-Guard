package com.android.dreamguard.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.capstone.dreamguard.databinding.ActivityHomeBinding
import com.android.dreamguard.ui.prediction.ActivityPrediction
import com.android.dreamguard.ui.settings.ActivitySettings
import com.android.dreamguard.utils.CustomBottomNavigation
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import com.capstone.dreamguard.R
import com.android.dreamguard.data.local.UserPreferences
import com.android.dreamguard.data.remote.models.LastPredictionCard
import com.android.dreamguard.ui.history.PredictionHistoryActivity
import com.android.dreamguard.ui.schedule.list.SleepSchedulerActivity
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.material.snackbar.Snackbar

class HomeActivity : ComponentActivity() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)


        userPreferences = UserPreferences(this)

        setContent {
            HomeScreen()
        }

        observeViewModel()
        viewModel.fetchHomePageData()
    }

    @Composable
    fun HomeScreen() {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AndroidView(
                factory = { context ->
                    val binding = ActivityHomeBinding.inflate(LayoutInflater.from(context))

                    // Update UI with user data
                    updateProfileSection(
                        binding.profilePicture,
                        binding.greetingText,
                        binding.dateText
                    )

                    binding.predictionHistoryButton.setOnClickListener { navigateToHistory() }
                    binding.sleepSchedulerButton.setOnClickListener { navigateToScheduler() }

                    binding.root
                },
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                CustomBottomNavigation(
                    currentTab = "Home",
                    onTabSelected = { selectedTab ->
                        when (selectedTab) {
                            "Home" -> {}
                            "Predict" -> navigateToPredict()
                            "Profile" -> navigateToProfile()
                        }
                    }
                )
            }
        }
    }

    private fun observeViewModel() {
        viewModel.homePageData.observe(this) { response ->
            response?.data?.let { data ->
                updateLastPredictionCard(data.lastPredictionCard)
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                Snackbar.make(findViewById(android.R.id.content), it, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun getFormattedDate(timestamp: String): String {
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = java.text.SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        return try {
            val date = inputFormat.parse(timestamp)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            Log.e("DateParse", "Error parsing date: ${e.message}")
            "Unknown date"
        }
    }

    private fun updateLastPredictionCard(lastPrediction: LastPredictionCard?) {
        lastPrediction?.let {
            val binding = ActivityHomeBinding.inflate(layoutInflater)
            binding.predictionTitle.text = "Your last prediction result is"
            binding.predictionResult.text = it.predictionResultText
            binding.lastPredictionDate.text = getFormattedDate(it.createdAt)
        }
    }
    
    private fun updateProfileSection(
        profileImageView: ImageView,
        greetingTextView: TextView,
        dateTextView: TextView
    ) {
        val userProfile = userPreferences.getUserProfile()
        val currentDate = getCurrentDate()
        val greetingMessage = getGreetingMessage()

        // Set greeting message
        greetingTextView.text = "$greetingMessage, ${userProfile?.name ?: "User"}"

        // Set current date
        dateTextView.text = currentDate

        // Load profile picture
        userProfile?.profilePicture?.let {
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.ic_launcher_background) // Placeholder
                .error(R.drawable.ic_launcher_background) // Error fallback
                .into(profileImageView)
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun getGreetingMessage(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour in 5..11 -> "Good Morning"
            hour in 12..17 -> "Good Afternoon"
            hour in 18..21 -> "Good Evening"
            else -> "Good Night"
        }
    }

    private fun navigateToPredict() {
        val intent = Intent(this, ActivityPrediction::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun navigateToProfile() {
        val intent = Intent(this, ActivitySettings::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun navigateToHistory() {
        val intent = Intent(this, PredictionHistoryActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun navigateToScheduler() {
        val intent = Intent(this, SleepSchedulerActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }
}
