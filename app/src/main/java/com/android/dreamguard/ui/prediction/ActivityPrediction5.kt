package com.android.dreamguard.ui.prediction

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.capstone.dreamguard.R
import com.android.dreamguard.data.local.datastore.PredictionDataStore
import com.capstone.dreamguard.databinding.ActivityPrediction5Binding

class ActivityPrediction5 : AppCompatActivity() {

    private lateinit var binding: ActivityPrediction5Binding
    private var selectedOccupationIndex: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrediction5Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDropdown()
        setupListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupDropdown() {
        val occupations = listOf(
            "Student", "Teacher", "Engineer", "Doctor", "Artist",
            "Freelancer", "Business Owner", "Others"
        )
        val adapter = ArrayAdapter(this, R.layout.dropdown_step_5, occupations)
        binding.exposedDropdown.setAdapter(adapter)

        binding.exposedDropdown.setOnItemClickListener { _, _, position, _ ->
            selectedOccupationIndex = position
        }
    }

    private fun setupListeners() {
        binding.predictButton.setOnClickListener {
            if (selectedOccupationIndex == null) {
                println("Please select an occupation")
                return@setOnClickListener
            }
            PredictionDataStore.occupation = selectedOccupationIndex!!
            proceedToNextStep()
        }
    }

    private fun proceedToNextStep() {
        val intent = Intent(this, ActivityPrediction6::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
