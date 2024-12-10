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
        initializeValues()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupListeners() {
        binding.iconPlusButtonStr.setOnClickListener {
            updateValue(binding.ageNumber1, 1)
        }
        binding.iconMinButton1.setOnClickListener {
            updateValue(binding.ageNumber1, -1)
        }

        binding.iconPlusButton2.setOnClickListener {
            updateValue(binding.ageNumber2, 1)
        }
        binding.iconMinButton2.setOnClickListener {
            updateValue(binding.ageNumber2, -1)
        }

        binding.iconPlusButton3.setOnClickListener {
            updateValue(binding.ageNumber3, 1)
        }
        binding.iconMinButton3.setOnClickListener {
            updateValue(binding.ageNumber3, -1)
        }

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

        when (textView) {
            binding.ageNumber1 -> PredictionDataStore.heartRate = newValue
            binding.ageNumber2 -> PredictionDataStore.systolic = newValue
            binding.ageNumber3 -> PredictionDataStore.diastolic = newValue
        }
    }

    private fun initializeValues() {
        binding.ageNumber1.text = (PredictionDataStore.heartRate.takeIf { it > 0 } ?: 90).toString()
        binding.ageNumber2.text = (PredictionDataStore.systolic.takeIf { it > 0 } ?: 160).toString()
        binding.ageNumber3.text = (PredictionDataStore.diastolic.takeIf { it > 0 } ?: 50).toString()
    }

    private fun validateInputs(): Boolean {
        return try {
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
