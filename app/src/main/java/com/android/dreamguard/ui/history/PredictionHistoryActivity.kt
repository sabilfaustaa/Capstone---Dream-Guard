package com.android.dreamguard.ui.history

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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

        adapter = PredictionAdapter(mutableListOf())
        binding.rvPredictions.apply {
            layoutManager = LinearLayoutManager(this@PredictionHistoryActivity)
            adapter = this@PredictionHistoryActivity.adapter
        }

        setupObservers()
        viewModel.fetchPredictionHistory()
    }

    private fun setupObservers() {
        viewModel.predictionHistory.observe(this) { predictions ->
            adapter.updateData(predictions)
        }

        viewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
