package com.android.dreamguard.ui.prediction

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.dreamguard.R
import com.android.dreamguard.data.local.datastore.PredictionDataStore
import com.capstone.dreamguard.databinding.ActivityPrediction2Binding

class ActivityPrediction2 : AppCompatActivity() {

    private lateinit var binding: ActivityPrediction2Binding
    private var age: Int = 25 // Default age value

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrediction2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupListeners()
        updateAgeDisplay()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupListeners() {
        binding.iconMinButton.setOnClickListener {
            if (age > 0) {
                age--
                updateAgeDisplay()
            }
        }

        binding.iconPlusButton.setOnClickListener {
            if (age < 150) {
                age++
                updateAgeDisplay()
            }
        }

        binding.predictButton.setOnClickListener {
            PredictionDataStore.age = age
            proceedToNextStep()
        }
    }

    private fun updateAgeDisplay() {
        binding.ageNumber.text = age.toString()
    }

    private fun proceedToNextStep() {
        if (age < 0 || age > 150) {
            println("Invalid age")
            return
        }
        val intent = Intent(this, ActivityPrediction3::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
