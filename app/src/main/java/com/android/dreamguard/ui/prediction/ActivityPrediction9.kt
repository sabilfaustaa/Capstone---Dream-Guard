package com.android.dreamguard.ui.prediction

import android.os.Bundle
import android.util.Log
import android.widget.TextView
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
import com.capstone.dreamguard.databinding.ActivityPrediction9Binding
import kotlinx.coroutines.launch
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ActivityPrediction9 : AppCompatActivity() {

    private lateinit var binding: ActivityPrediction9Binding
    private lateinit var tfliteInterpreter: Interpreter
    private val viewModel: PredictionViewModel by viewModels {
        val repository = ModelRepository(ApiConfig.getApiService())
        PredictionViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrediction9Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupListeners()
        setupObservers()

        viewModel.fetchLatestModelUrl()
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

        // Predict button
        binding.predictButton.setOnClickListener {
            if (validateInputs()) {
                lifecycleScope.launch {
                    try {
                        val modelUrl = viewModel.modelResponse.value?.modelUrl
                            ?: throw IllegalStateException("Model URL is not available.")
                        val modelFile = ModelDownloader.downloadModel(this@ActivityPrediction9, modelUrl)
                        tfliteInterpreter = PredictionModelManager.loadInterpreter(modelFile)

                        val predictionResult = runPrediction()
                        showPredictionResult(predictionResult)
                    } catch (e: Exception) {
                        showError("Error during prediction: ${e.message}")
                    }
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.modelResponse.observe(this) { response ->
            response.modelUrl?.let { url ->
                Log.d("ActivityPrediction9", "Model URL: $url")
            } ?: run {
                showError(response.message)
            }
        }

        viewModel.error.observe(this) {
            showError(it)
        }
    }

    private fun updateValue(textView: TextView, increment: Int) {
        val currentValue = textView.text.toString().toInt()
        val newValue = (currentValue + increment).coerceAtLeast(0)
        textView.text = newValue.toString()
    }

    private fun validateInputs(): Boolean {
        return try {
            PredictionDataStore.heartRate = binding.ageNumber1.text.toString().toInt()
            PredictionDataStore.systolic = binding.ageNumber2.text.toString().toInt()
            PredictionDataStore.diastolic = binding.ageNumber3.text.toString().toInt()
            true
        } catch (e: NumberFormatException) {
            showError("Please ensure all inputs are valid numbers.")
            false
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
        val inputBuffer = ByteBuffer.allocateDirect(4 * 12).apply {
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

    private fun showPredictionResult(result: FloatArray) {
        Log.d("PredictionResult", result.toString())
        val formattedResults = result.joinToString(", ") { "%.2f".format(it) }
        binding.describePrediction.text = getString(R.string.prediction_result, formattedResults)
    }

    private fun showError(message: String?) {
        val errorMessage = message ?: "Unknown error"
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        Log.e("ActivityPrediction9", errorMessage)
    }
}
