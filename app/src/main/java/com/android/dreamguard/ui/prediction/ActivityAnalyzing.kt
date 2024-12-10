package com.android.dreamguard.ui.prediction

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.dreamguard.data.local.datastore.PredictionDataStore
import com.android.dreamguard.data.remote.api.ApiConfig
import com.android.dreamguard.data.repository.PredictionRepository
import com.android.dreamguard.ui.result.ActivityResultInsomnia
import com.android.dreamguard.ui.result.ActivityResultNoSleepDisorder
import com.android.dreamguard.ui.result.ActivityResultNoSleepDisorderStress
import com.android.dreamguard.ui.result.ActivityResultNoSleepDisorderUnder8
import com.android.dreamguard.ui.result.ActivityResultNoSleepDisorderUnderStress
import com.android.dreamguard.ui.result.ActivityResultSleepApnea
import com.capstone.dreamguard.databinding.ActivityAnalyzingBinding
import kotlinx.coroutines.launch

class ActivityAnalyzing : AppCompatActivity() {

    private lateinit var binding: ActivityAnalyzingBinding
    private val viewModel: PredictionViewModel by viewModels {
        val repository = PredictionRepository(ApiConfig.getApiService(this))
        PredictionViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyzingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        submitPrediction()
    }

    private fun submitPrediction() {
        val gender = if (PredictionDataStore.gender == "male") 2 else 1
        val predictionData = mapOf(
            "gender" to gender,
            "age" to PredictionDataStore.age,
            "sleepDuration" to PredictionDataStore.hoursOfSleep,
            "sleepQuality" to PredictionDataStore.sleepQuality,
            "occupation" to PredictionDataStore.occupation,
            "activityLevel" to PredictionDataStore.activityLevel,
            "stressLevel" to PredictionDataStore.stressLevel,
            "weight" to PredictionDataStore.weight,
            "height" to PredictionDataStore.height,
            "heartRate" to PredictionDataStore.heartRate,
            "dailySteps" to PredictionDataStore.dailySteps,
            "systolic" to PredictionDataStore.systolic,
            "diastolic" to PredictionDataStore.diastolic
        )

        lifecycleScope.launch {
            viewModel.submitPrediction(predictionData)
            observePredictionResult()
        }
    }

    private fun observePredictionResult() {
        viewModel.predictionResult.observe(this) { response ->
            if (response.isSuccessful) {
                response.body()?.let {
                    handlePredictionResult(it.data.prediction.id)
                } ?: showError("Prediction failed: No data found.")
            } else {
                showError("Prediction failed: ${response.message()}")
            }
        }

        viewModel.error.observe(this) {
            showError(it)
        }
    }

    private fun handlePredictionResult(predictionId: Int) {
        when (predictionId) {
            1 -> navigateTo(ActivityResultNoSleepDisorder::class.java)
            2 -> navigateTo(ActivityResultSleepApnea::class.java)
            3 -> navigateTo(ActivityResultInsomnia::class.java)
            4 -> navigateTo(ActivityResultNoSleepDisorderUnder8::class.java)
            5 -> navigateTo(ActivityResultNoSleepDisorderStress::class.java)
            6 -> navigateTo(ActivityResultNoSleepDisorderUnderStress::class.java)
            else -> showError("Unknown prediction result.")
        }
    }

    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
        finish()
    }

    private fun showError(message: String?) {
        val errorMessage = message ?: "Unknown error"
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        Log.e("ActivityAnalyzing", errorMessage)
    }
}
