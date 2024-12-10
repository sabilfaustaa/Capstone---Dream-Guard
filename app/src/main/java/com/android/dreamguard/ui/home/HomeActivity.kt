package com.android.dreamguard.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.view.LayoutInflater
import androidx.compose.ui.viewinterop.AndroidView
import com.capstone.dreamguard.databinding.ActivityHomeBinding
import com.android.dreamguard.ui.prediction.ActivityPrediction
import com.android.dreamguard.ui.settings.ActivitySettings
import com.android.dreamguard.utils.CustomBottomNavigation

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen()
        }
    }

    private fun navigateToPredict() {
        startActivity(Intent(this, ActivityPrediction::class.java))
    }

    private fun navigateToProfile() {
        startActivity(Intent(this, ActivitySettings::class.java))
    }

    @Composable
    fun HomeScreen() {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Sisipkan layout XML menggunakan ViewBinding
            AndroidView(
                factory = { context ->
                    val binding = ActivityHomeBinding.inflate(LayoutInflater.from(context))
                    binding.root
                },
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                CustomBottomNavigation { selectedTab ->
                    when (selectedTab) {
                        "Home" -> {
                        }
                        "Predict" -> {
                            navigateToPredict()
                        }
                        "Profile" -> {
                            navigateToProfile()
                        }
                    }
                }
            }
        }
    }
}
