package com.android.dreamguard.ui.prediction

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.dreamguard.R
import com.android.dreamguard.data.local.datastore.PredictionDataStore
import com.capstone.dreamguard.databinding.ActivityPrediction7Binding

class ActivityPrediction7 : AppCompatActivity() {

    private lateinit var binding: ActivityPrediction7Binding
    private var weight: Int = 60
    private var height: Int = 170

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrediction7Binding.inflate(layoutInflater)
        setContentView(binding.root)

        weight = PredictionDataStore.weight.takeIf { it > 0 } ?: 60
        height = PredictionDataStore.height.takeIf { it > 0 } ?: 170

        setupToolbar()
        setupListeners()
        updateWeightDisplay()
        updateHeightDisplay()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupListeners() {
        // Weight
        binding.iconMinButtonWeight.setOnClickListener {
            if (weight > 1) {
                weight--
                updateWeightDisplay()
            }
        }

        binding.iconPlusButtonWeight.setOnClickListener {
            if (weight < 300) { // Max weight threshold
                weight++
                updateWeightDisplay()
            }
        }

        // Height
        binding.iconMinButtonHeight.setOnClickListener {
            if (height > 50) { // Min height threshold
                height--
                updateHeightDisplay()
            }
        }

        binding.iconPlusButtonHeight.setOnClickListener {
            if (height < 250) { // Max height threshold
                height++
                updateHeightDisplay()
            }
        }

        binding.predictButton.setOnClickListener {
            PredictionDataStore.weight = weight
            PredictionDataStore.height = height
            proceedToNextStep()
        }
    }

    private fun updateWeightDisplay() {
        binding.weightNumber.setText("$weight kg")
    }

    private fun updateHeightDisplay() {
        binding.heightNumber.setText("$height cm")
    }

    private fun proceedToNextStep() {
        if (weight < 1 || weight > 300 || height < 50 || height > 250) {
            println("Invalid weight or height values")
            return
        }
        val intent = Intent(this, ActivityPrediction8::class.java)
        startActivity(intent)
    }
}
