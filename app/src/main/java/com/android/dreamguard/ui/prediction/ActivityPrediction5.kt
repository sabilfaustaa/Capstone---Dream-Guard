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
    private var selectedOccupationValue: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrediction5Binding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedOccupationValue = PredictionDataStore.occupation

        setupToolbar()
        setupDropdown()
        setupListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupDropdown() {
        val occupationMapping = mapOf(
            "Software Engineer" to 1,
            "Doctor" to 2,
            "Sales Representative" to 3,
            "Teacher" to 4,
            "Nurse" to 5,
            "Engineer" to 6,
            "Accountant" to 7,
            "Scientist" to 8,
            "Lawyer" to 9,
            "Salesperson" to 10,
            "Manager" to 11
        )

        val occupations = occupationMapping.keys.toList()
        val adapter = ArrayAdapter(this, R.layout.dropdown_step_5, occupations)
        binding.exposedDropdown.setAdapter(adapter)

        binding.exposedDropdown.setOnItemClickListener { _, _, position, _ ->
            val selectedOccupation = occupations[position]
            selectedOccupationValue = occupationMapping[selectedOccupation]
        }
    }

    private fun setupListeners() {
        binding.predictButton.setOnClickListener {
            if (selectedOccupationValue == null) {
                println("Please select an occupation")
                return@setOnClickListener
            }
            PredictionDataStore.occupation = selectedOccupationValue!!
            proceedToNextStep()
        }
    }

    private fun proceedToNextStep() {
        val intent = Intent(this, ActivityPrediction6::class.java)
        startActivity(intent)
    }
}
