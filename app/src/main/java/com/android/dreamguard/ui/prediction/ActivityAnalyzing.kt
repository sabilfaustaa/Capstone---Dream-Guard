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
import com.android.dreamguard.data.repository.ModelRepository
import com.android.dreamguard.utils.ModelDownloader
import com.android.dreamguard.utils.PredictionModelManager
import com.capstone.dreamguard.R
import com.capstone.dreamguard.databinding.ActivityAnalyzingBinding
import kotlinx.coroutines.launch
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ActivityAnalyzing : AppCompatActivity() {

    private lateinit var binding: ActivityAnalyzingBinding
    private lateinit var tfliteInterpreter: Interpreter
    private val viewModel: PredictionViewModel by viewModels {
        val repository = ModelRepository(ApiConfig.getApiService())
        PredictionViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyzingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        viewModel.fetchLatestModelUrl()
    }

    private fun setupObservers() {
        viewModel.modelResponse.observe(this) { response ->
            response.modelUrl?.let { url ->
                Log.d("ActivityAnalyzing", "Model URL: $url")
                downloadAndPredict(url)
            } ?: run {
                showError(response.message)
            }
        }

        viewModel.error.observe(this) {
            showError(it)
        }
    }

    private fun downloadAndPredict(url: String) {
        lifecycleScope.launch {
            try {
                val modelFile = ModelDownloader.downloadModel(this@ActivityAnalyzing, url)
                tfliteInterpreter = PredictionModelManager.loadInterpreter(modelFile)
                val predictionResult = runPrediction()
                handlePredictionResult(predictionResult)
            } catch (e: Exception) {
                showError("Error in downloadAndPredict: ${e.message}")
            }
        }
    }

    private fun runPrediction(): FloatArray {
        val inputBuffer = prepareInputData()
        val outputBuffer = ByteBuffer.allocateDirect(4 * 3).apply {
            order(ByteOrder.nativeOrder())
        }
        tfliteInterpreter.run(inputBuffer, outputBuffer)

        outputBuffer.rewind()
        val predictions = FloatArray(3)
        for (i in predictions.indices) {
            predictions[i] = outputBuffer.float
        }
        return predictions
    }

    private fun prepareInputData(): ByteBuffer {
        val inputFeatureCount = 12
        val inputBuffer = ByteBuffer.allocateDirect(4 * inputFeatureCount).apply {
            order(ByteOrder.nativeOrder())
        }

        val genderValue = when (PredictionDataStore.gender) {
            "male" -> 0.0f
            "female" -> 1.0f
            else -> throw IllegalArgumentException("Invalid gender value: ${PredictionDataStore.gender}")
        }
        inputBuffer.putFloat(genderValue)

        inputBuffer.putFloat(PredictionDataStore.age.toFloat())
        inputBuffer.putFloat(PredictionDataStore.hoursOfSleep.toFloat())
        inputBuffer.putFloat(PredictionDataStore.occupation.toFloat())
        inputBuffer.putFloat(PredictionDataStore.activityLevel.toFloat())
        inputBuffer.putFloat(PredictionDataStore.stressLevel.toFloat())
        inputBuffer.putFloat(PredictionDataStore.weight.toFloat())
        inputBuffer.putFloat(PredictionDataStore.height.toFloat())
        inputBuffer.putFloat(PredictionDataStore.heartRate.toFloat())
        inputBuffer.putFloat(PredictionDataStore.systolic.toFloat())
        inputBuffer.putFloat(PredictionDataStore.diastolic.toFloat())
        inputBuffer.putFloat(0.0f)

        inputBuffer.rewind()
        return inputBuffer
    }

    private fun handlePredictionResult(result: FloatArray) {
        val maxIndex = result.indexOfFirst { it == result.maxOrNull() }
        when (maxIndex) {
            0 -> handleNoSleepDisorder()
            1 -> navigateTo(ActivityResultSleepApnea::class.java)
            2 -> navigateTo(ActivityResultInsomnia::class.java)
            else -> showError("Unknown prediction result.")
        }
    }

    private fun handleNoSleepDisorder() {
        val hoursOfSleep = PredictionDataStore.hoursOfSleep
        val stressLevel = PredictionDataStore.stressLevel

        when {
            hoursOfSleep < 8 && stressLevel in 8..10 -> {
                navigateTo(ActivityResultNoSleepDisorderUnderStress::class.java)
            }
            hoursOfSleep < 8 -> {
                navigateTo(ActivityResultNoSleepDisorderUnder8::class.java)
            }
            stressLevel in 8..10 -> {
                navigateTo(ActivityResultNoSleepDisorderStress::class.java)
            }
            else -> {
                navigateTo(ActivityResultNoSleepDisorder::class.java)
            }
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
