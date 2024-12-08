package com.android.dreamguard.ui.prediction

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.dreamguard.R
import com.android.dreamguard.data.local.datastore.PredictionDataStore
import com.capstone.dreamguard.databinding.ActivityPrediction6Binding

class ActivityPrediction6 : AppCompatActivity() {

    private lateinit var binding: ActivityPrediction6Binding
    private var activityLevel: Int = 5 // Default activity level
    private var stressLevel: Int = 5 // Default stress level

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrediction6Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupListeners()
        updateActivityLevelDisplay()
        updateStressLevelDisplay()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupListeners() {
        // Activity Level
        binding.iconMinButton.setOnClickListener {
            if (activityLevel > 1) {
                activityLevel--
                updateActivityLevelDisplay()
            }
        }

        binding.iconPlusButton.setOnClickListener {
            if (activityLevel < 10) {
                activityLevel++
                updateActivityLevelDisplay()
            }
        }

        // Stress Level
        binding.iconMinButtonStr.setOnClickListener {
            if (stressLevel > 1) {
                stressLevel--
                updateStressLevelDisplay()
            }
        }

        binding.iconPlusButtonStr.setOnClickListener {
            if (stressLevel < 10) {
                stressLevel++
                updateStressLevelDisplay()
            }
        }

        binding.predictButton.setOnClickListener {
            PredictionDataStore.activityLevel = activityLevel
            PredictionDataStore.stressLevel = stressLevel
            proceedToNextStep()
        }
    }

    private fun updateActivityLevelDisplay() {
        binding.ageNumber.text = activityLevel.toString()
    }

    private fun updateStressLevelDisplay() {
        binding.ageNumberStr.text = stressLevel.toString()
    }

    private fun proceedToNextStep() {
        if (activityLevel < 1 || activityLevel > 10 || stressLevel < 1 || stressLevel > 10) {
            println("Invalid activity or stress level values")
            return
        }
        val intent = Intent(this, ActivityPrediction7::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
