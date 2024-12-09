package com.android.dreamguard.ui.prediction

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.dreamguard.R
import com.android.dreamguard.data.local.datastore.PredictionDataStore
import com.capstone.dreamguard.databinding.ActivityPrediction6Binding

class ActivityPrediction6 : AppCompatActivity() {

    private lateinit var binding: ActivityPrediction6Binding
    private var activityLevel: Int = 5
    private var stressLevel: Int = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrediction6Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set initial values from PredictionDataStore
        activityLevel = PredictionDataStore.activityLevel.takeIf { it in 1..100 } ?: 5
        stressLevel = PredictionDataStore.stressLevel.takeIf { it in 1..100 } ?: 5

        setupToolbar()
        setupListeners()
        updateActivityLevelDisplay()
        updateStressLevelDisplay()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupListeners() {
        // Listeners for Activity Level
        binding.iconMinButtonAct.setOnClickListener {
            if (activityLevel > 1) {
                activityLevel--
                updateActivityLevelDisplay()
            }
        }

        binding.iconPlusButtonAct.setOnClickListener {
            if (activityLevel < 100) {
                activityLevel++
                updateActivityLevelDisplay()
            }
        }

        // Listeners for Stress Level
        binding.iconMinButtonStr.setOnClickListener {
            if (stressLevel > 1) {
                stressLevel--
                updateStressLevelDisplay()
            }
        }

        binding.iconPlusButtonStr.setOnClickListener {
            if (stressLevel < 100) {
                stressLevel++
                updateStressLevelDisplay()
            }
        }

        // Predict button listener
        binding.predictButton.setOnClickListener {
            // Update PredictionDataStore with new values
            PredictionDataStore.activityLevel = activityLevel
            PredictionDataStore.stressLevel = stressLevel
            proceedToNextStep()
        }
    }

    private fun updateActivityLevelDisplay() {
        binding.ageNumberAct.text = activityLevel.toString()
    }

    private fun updateStressLevelDisplay() {
        binding.ageNumberStr.text = stressLevel.toString()
    }

    private fun proceedToNextStep() {
        if (activityLevel !in 1..100 || stressLevel !in 1..100) {
            // Ensure valid range before proceeding
            println("Invalid activity or stress level values")
            return
        }
        // Navigate to the next activity
        val intent = Intent(this, ActivityPrediction7::class.java)
        startActivity(intent)
    }
}
