package com.android.dreamguard.ui.home

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.runtime.Composable
import com.android.dreamguard.ui.history.PredictionHistoryActivity

class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen()
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

    @Composable
    fun HomeScreen() {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AndroidView(
                factory = { context ->
                    val binding = ActivityHomeBinding.inflate(LayoutInflater.from(context))

                    binding.predictionHistoryButton.setOnClickListener { navigateToHistory() }

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
                            "Home" -> { /* Stay on Home */ }
                            "Predict" -> navigateToPredict()
                            "Profile" -> navigateToProfile()
                        }
                    }
                )
            }
        }
    }

    private fun navigateToHistory() {
        val intent = Intent(this, PredictionHistoryActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }
}
