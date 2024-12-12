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
            updateValue(binding.heartRateNumber, 1)
        }
        binding.iconMinButton1.setOnClickListener {
            updateValue(binding.heartRateNumber, -1)
        }

        binding.iconPlusButton2.setOnClickListener {
            updateValue(binding.systolicNumber, 1)
        }
        binding.iconMinButton2.setOnClickListener {
            updateValue(binding.systolicNumber, -1)
        }

        binding.iconPlusButton3.setOnClickListener {
            updateValue(binding.diastolicNumber, 1)
        }
        binding.iconMinButton3.setOnClickListener {
            updateValue(binding.diastolicNumber, -1)
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
            binding.heartRateNumber -> PredictionDataStore.heartRate = newValue
            binding.systolicNumber -> PredictionDataStore.systolic = newValue
            binding.diastolicNumber -> PredictionDataStore.diastolic = newValue
        }
    }

    private fun initializeValues() {
        binding.heartRateNumber.setText((PredictionDataStore.heartRate.takeIf { it > 0 } ?: 90).toString())
        binding.systolicNumber.setText((PredictionDataStore.systolic.takeIf { it > 0 } ?: 160).toString())
        binding.diastolicNumber.setText((PredictionDataStore.diastolic.takeIf { it > 0 } ?: 50).toString())

    }

    private fun validateInputs(): Boolean {
        return try {
            binding.heartRateNumber.text.toString().toInt()
            binding.systolicNumber.text.toString().toInt()
            binding.diastolicNumber.text.toString().toInt()
            true
        } catch (e: NumberFormatException) {
            showError("Please ensure all inputs are valid numbers.")
            false
        }
    }

    private fun saveInputData() {
        try {
            PredictionDataStore.heartRate = binding.heartRateNumber.text.toString().toInt()
            PredictionDataStore.systolic = binding.systolicNumber.text.toString().toInt()
            PredictionDataStore.diastolic = binding.diastolicNumber.text.toString().toInt()
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
