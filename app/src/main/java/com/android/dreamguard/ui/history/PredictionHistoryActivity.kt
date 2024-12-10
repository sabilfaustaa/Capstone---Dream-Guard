package com.android.dreamguard.ui.history

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.dreamguard.data.remote.models.PredictionItem
import com.android.dreamguard.ui.home.HomeActivity
import com.android.dreamguard.ui.result.ActivityResultInsomnia
import com.android.dreamguard.ui.result.ActivityResultNoSleepDisorder
import com.android.dreamguard.ui.result.ActivityResultNoSleepDisorderStress
import com.android.dreamguard.ui.result.ActivityResultNoSleepDisorderUnder8
import com.android.dreamguard.ui.result.ActivityResultNoSleepDisorderUnderStress
import com.android.dreamguard.ui.result.ActivityResultSleepApnea
import com.capstone.dreamguard.databinding.ActivityPredictionHistoryBinding

class PredictionHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPredictionHistoryBinding
    private lateinit var adapter: PredictionAdapter
    private val viewModel: PredictionHistoryViewModel by viewModels {
        PredictionHistoryViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPredictionHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            navigateToHome()
        }

        adapter = PredictionAdapter(mutableListOf()) { predictionId ->
            handlePredictionButton(predictionId)
        }
        binding.rvPredictions.apply {
            layoutManager = LinearLayoutManager(this@PredictionHistoryActivity)
            adapter = this@PredictionHistoryActivity.adapter
        }

        setupObservers()

        fetchFilteredPredictions("")

        binding.btnAllHistory.setOnClickListener {
            fetchFilteredPredictions("")
        }
        binding.btnNoDisorderHistory.setOnClickListener {
            fetchFilteredPredictions("No Sleep Disorder")
        }
        binding.btnApneaHistory.setOnClickListener {
            fetchFilteredPredictions("Sleep Apnea")
        }
        binding.btnInsomniaHistory.setOnClickListener {
            fetchFilteredPredictions("Sleep Insomnia")
        }
    }

    private fun setupObservers() {
        viewModel.predictionHistory.observe(this) { predictions ->
            adapter.updateData(predictions)
        }

        viewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchFilteredPredictions(filter: String) {
        viewModel.fetchPredictionHistory(filter)
    }

    private fun handlePredictionButton(predictionId: Int) {
        when (predictionId) {
            1 -> navigateTo(ActivityResultNoSleepDisorder::class.java)
            2 -> navigateTo(ActivityResultSleepApnea::class.java)
            3 -> navigateTo(ActivityResultInsomnia::class.java)
            4 -> navigateTo(ActivityResultNoSleepDisorderUnder8::class.java)
            5 -> navigateTo(ActivityResultNoSleepDisorderStress::class.java)
            6 -> navigateTo(ActivityResultNoSleepDisorderUnderStress::class.java)
            else -> Toast.makeText(this, "Unknown prediction result.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
}