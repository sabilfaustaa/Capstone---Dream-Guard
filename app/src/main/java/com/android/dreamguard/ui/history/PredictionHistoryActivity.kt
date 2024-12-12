package com.android.dreamguard.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.dreamguard.ui.home.HomeActivity
import com.android.dreamguard.ui.result.ActivityResultInsomnia
import com.android.dreamguard.ui.result.ActivityResultNoSleepDisorder
import com.android.dreamguard.ui.result.ActivityResultNoSleepDisorderStress
import com.android.dreamguard.ui.result.ActivityResultNoSleepDisorderUnder8
import com.android.dreamguard.ui.result.ActivityResultNoSleepDisorderUnderStress
import com.android.dreamguard.ui.result.ActivityResultSleepApnea
import com.capstone.dreamguard.databinding.ActivityPredictionHistoryBinding

import androidx.core.content.ContextCompat
import android.widget.Button
import com.capstone.dreamguard.R

class PredictionHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPredictionHistoryBinding
    private lateinit var adapter: PredictionAdapter
    private val viewModel: PredictionHistoryViewModel by viewModels {
        PredictionHistoryViewModelFactory(this)
    }
    private var selectedButton: Button? = null

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

        changeButtonColor(binding.btnAllHistory)

        fetchFilteredPredictions("")

        binding.btnAllHistory.setOnClickListener {
            changeButtonColor(binding.btnAllHistory)
            fetchFilteredPredictions("")
        }
        binding.btnNoDisorderHistory.setOnClickListener {
            changeButtonColor(binding.btnNoDisorderHistory)
            fetchFilteredPredictions("No Sleep Disorder")
        }
        binding.btnApneaHistory.setOnClickListener {
            changeButtonColor(binding.btnApneaHistory)
            fetchFilteredPredictions("Sleep Apnea")
        }
        binding.btnInsomniaHistory.setOnClickListener {
            changeButtonColor(binding.btnInsomniaHistory)
            fetchFilteredPredictions("Sleep Insomnia")
        }
    }

    private fun setupObservers() {
        viewModel.predictionHistory.observe(this) { predictions ->
            adapter.updateData(predictions)
            showLoading(false)
        }

        viewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            showLoading(false)
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


    private fun changeButtonColor(selected: Button) {
        selectedButton?.setBackgroundColor(ContextCompat.getColor(this, R.color.white))


        selected.setBackgroundColor(ContextCompat.getColor(this, R.color.teal))

        selectedButton = selected
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

}
