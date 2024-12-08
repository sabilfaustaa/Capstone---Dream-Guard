package com.android.dreamguard.ui.prediction

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.dreamguard.R
import com.capstone.dreamguard.databinding.ActivityPrediction9Binding
import com.android.dreamguard.data.local.datastore.PredictionDataStore

class ActivityPrediction9 : AppCompatActivity() {

    private lateinit var binding: ActivityPrediction9Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrediction9Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupListeners() {
        // Increment and decrement buttons for heart rate
        binding.iconPlusButtonStr.setOnClickListener {
            updateValue(binding.ageNumber1, 1)
        }
        binding.iconMinButton1.setOnClickListener {
            updateValue(binding.ageNumber1, -1)
        }

        // Increment and decrement buttons for systolic pressure
        binding.iconPlusButton2.setOnClickListener {
            updateValue(binding.ageNumber2, 1)
        }
        binding.iconMinButton2.setOnClickListener {
            updateValue(binding.ageNumber2, -1)
        }

        // Increment and decrement buttons for diastolic pressure
        binding.iconPlusButton3.setOnClickListener {
            updateValue(binding.ageNumber3, 1)
        }
        binding.iconMinButton3.setOnClickListener {
            updateValue(binding.ageNumber3, -1)
        }

        // Next button to go to analyzing
        binding.predictButton.setOnClickListener {
            if (validateInputs()) {
                saveInputData()
                navigateToAnalyzingPage()
            }
        }
    }

    private fun updateValue(textView: TextView, increment: Int) {
        val currentValue = textView.text.toString().toInt()
        val newValue = (currentValue + increment).coerceAtLeast(0)
        textView.text = newValue.toString()
    }

    private fun validateInputs(): Boolean {
        return try {
            // Check inputs
            binding.ageNumber1.text.toString().toInt()
            binding.ageNumber2.text.toString().toInt()
            binding.ageNumber3.text.toString().toInt()
            true
        } catch (e: NumberFormatException) {
            showError("Please ensure all inputs are valid numbers.")
            false
        }
    }

    private fun saveInputData() {
        try {
            PredictionDataStore.heartRate = binding.ageNumber1.text.toString().toInt()
            PredictionDataStore.systolic = binding.ageNumber2.text.toString().toInt()
            PredictionDataStore.diastolic = binding.ageNumber3.text.toString().toInt()

            Toast.makeText(this, "Input saved successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            showError("Error saving input data: ${e.message}")
        }
    }

    private fun navigateToAnalyzingPage() {
        val intent = Intent(this, ActivityAnalyzing::class.java)
        startActivity(intent)
        finish()
    }

    private fun showError(message: String?) {
        val errorMessage = message ?: "Unknown error"
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }
}
