package com.android.dreamguard.ui.prediction

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.dreamguard.R
import com.android.dreamguard.data.local.datastore.PredictionDataStore
import com.capstone.dreamguard.databinding.ActivityPrediction8Binding

class ActivityPrediction8 : AppCompatActivity() {

    private lateinit var binding: ActivityPrediction8Binding
    private var dailySteps: Int = 5000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrediction8Binding.inflate(layoutInflater)
        setContentView(binding.root)

        dailySteps = PredictionDataStore.dailySteps.takeIf { it > 0 } ?: 5000

        setupToolbar()
        setupListeners()
        updateStepsDisplay()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupListeners() {
        binding.iconMinButtonStr.setOnClickListener {
            if (dailySteps > 0) {
                dailySteps -= 500 // Decrease by 500 steps
                updateStepsDisplay()
            }
        }

        binding.iconPlusButtonStr.setOnClickListener {
            if (dailySteps < 50000) { // Max threshold for steps
                dailySteps += 500 // Increase by 500 steps
                updateStepsDisplay()
            }
        }

        binding.predictButton.setOnClickListener {
            PredictionDataStore.dailySteps = dailySteps
            proceedToNextStep()
        }
    }

    private fun updateStepsDisplay() {
        binding.stepsNumber.setText("$dailySteps steps")
    }
    private fun proceedToNextStep() {
        if (dailySteps < 0 || dailySteps > 50000) {
            println("Invalid daily steps value")
            return
        }
        val intent = Intent(this, ActivityPrediction9::class.java)
        startActivity(intent)
    }
}
