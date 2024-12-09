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
import com.capstone.dreamguard.databinding.ActivityAnalyzingBinding
import kotlinx.coroutines.launch
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.pow

class ActivityAnalyzing : AppCompatActivity() {

    private lateinit var binding: ActivityAnalyzingBinding
    private lateinit var tfliteInterpreter: Interpreter
    private val viewModel: PredictionViewModel by viewModels {
        val repository = ModelRepository(ApiConfig.getApiService())
        PredictionViewModelFactory(repository)
    }

    private val disorderMapping = mapOf(
        0 to "No Sleep Disorder",
        1 to "Sleep Apnea",
        2 to "Insomnia"
    )

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
                logInputData()
                downloadAndPredict(url)
            } ?: run {
                showError(response.message)
            }
        }

        viewModel.error.observe(this) {
            showError(it)
        }
    }

    private fun logInputData() {
        try {
            Log.d(
                "ActivityAnalyzing",
                """
                Input Data:
                Gender: ${PredictionDataStore.gender}
                Age: ${PredictionDataStore.age}
                Hours of Sleep: ${PredictionDataStore.hoursOfSleep}
                Quality of Sleep: ${PredictionDataStore.sleepQuality}
                Occupation: ${PredictionDataStore.occupation}
                Activity Level: ${PredictionDataStore.stressLevel}
                Stress Level: ${PredictionDataStore.activityLevel}
                Weight: ${PredictionDataStore.weight}
                Height: ${PredictionDataStore.height}
                Heart Rate: ${PredictionDataStore.heartRate}
                Daily Steps: ${PredictionDataStore.dailySteps}
                Systolic: ${PredictionDataStore.systolic}
                Diastolic: ${PredictionDataStore.diastolic}
            """.trimIndent()
            )
        } catch (e: Exception) {
            showError("Error logging input data: ${e.message}")
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
        return try {
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

            Log.d("ActivityAnalyzing", "Prediction Results: ${predictions.joinToString(", ")}")
            predictions
        } catch (e: Exception) {
            showError("Error in runPrediction: ${e.message}")
            floatArrayOf()
        }
    }

    private fun scaleInput(input: FloatArray): FloatArray {
        val means = floatArrayOf(
            1.43478261f,   // Gender
            44.1594203f,   // Age
            5.84057971f,   // Occupation
            6.63043478f,   // Hours of Sleep
            7.10144928f,   // Quality of Sleep
            59.4420290f,   // Activity Level
            5.63768116f,   // Stress Level
            1.65217391f,   // BMI Category
            70.9202899f,   // Heart Rate
            6850.72464f,   // Daily Steps
            131.275362f,   // Systolic
            86.8333333f    // Diastolic
        )

        val stdDevs = floatArrayOf(
            0.49572347f,   // Gender (sqrt(0.245746692))
            8.68216165f,   // Age (sqrt(75.3803823))
            2.49708911f,   // Occupation (sqrt(6.23545474))
            0.80816932f,   // Hours of Sleep (sqrt(0.653276623))
            1.27002931f,   // Quality of Sleep (sqrt(1.61289645))
            21.011784f,    // Activity Level (sqrt(441.7249))
            1.86036885f,   // Stress Level (sqrt(3.46292796))
            0.54752371f,   // BMI Category (sqrt(0.299306868))
            4.40365869f,   // Heart Rate (sqrt(19.392197))
            1766.78844f,   // Daily Steps (sqrt(3121919.76))
            7.67787625f,   // Systolic (sqrt(58.9096828))
            6.36439379f    // Diastolic (sqrt(40.4867150))
        )

        return input.mapIndexed { index, value ->
            (value - means[index]) / stdDevs[index]
        }.toFloatArray()
    }

    private fun prepareInputData(): ByteBuffer {
        try {
            val gender = when (PredictionDataStore.gender) {
                "male" -> 2.0f
                "female" -> 1.0f
                else -> throw IllegalArgumentException("Invalid gender value")
            }

            val bmi = PredictionDataStore.weight.toFloat() / ((PredictionDataStore.height.toFloat() / 100).pow(2))
            val bmiCategory = calculateBmiCategory(bmi)

//            2.0, 29.0, 4.0, 6.0, 6.0, 7.0, 40.0, 3.0, 82.0, 3500.0, 140.0, 90.0
            val rawInput = floatArrayOf(
                gender,
                PredictionDataStore.age.toFloat(),
                PredictionDataStore.occupation.toFloat(),
                PredictionDataStore.hoursOfSleep.toFloat(),
                PredictionDataStore.sleepQuality.toFloat(),
                PredictionDataStore.stressLevel.toFloat(),
                PredictionDataStore.activityLevel.toFloat(),
                bmiCategory.toFloat(),
                PredictionDataStore.heartRate.toFloat(),
                PredictionDataStore.dailySteps.toFloat(),
                PredictionDataStore.systolic.toFloat(),
                PredictionDataStore.diastolic.toFloat()
            )

            Log.d("ActivityAnalyzing", "Raw Input (before scaling): ${rawInput.joinToString(", ")}")

            // Scale input
            val scaledInput = scaleInput(rawInput)

            Log.d("ActivityAnalyzing", "Scaled Input: ${scaledInput.joinToString(", ")}")

            val inputBuffer = ByteBuffer.allocateDirect(4 * scaledInput.size).apply {
                order(ByteOrder.nativeOrder())
            }
            scaledInput.forEach { inputBuffer.putFloat(it) }
            inputBuffer.rewind()

            return inputBuffer
        } catch (e: Exception) {
            throw IllegalArgumentException("Error preparing input data: ${e.message}")
        }
    }

    private fun calculateBmiCategory(bmi: Float): Int {
        return when {
            bmi <= 24.9 -> 1 // Normal
            bmi in 25.0..29.9 -> 2 // Overweight
            else -> 3 // Obese
        }
    }

    private fun handlePredictionResult(result: FloatArray) {
        if (result.isEmpty()) {
            showError("Prediction failed: result array is empty.")
            return
        }

        val maxIndex = result.indices.maxByOrNull { result[it] } ?: -1
        val confidence = result.getOrNull(maxIndex) ?: 0.0f

        Log.d("ActivityAnalyzing", "Prediction Index: $maxIndex, Confidence: $confidence, Result: ${disorderMapping[maxIndex]}")

        when (maxIndex) {
            0 -> navigateTo(ActivityResultNoSleepDisorder::class.java)
            1 -> navigateTo(ActivityResultSleepApnea::class.java)
            2 -> navigateTo(ActivityResultInsomnia::class.java)
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
