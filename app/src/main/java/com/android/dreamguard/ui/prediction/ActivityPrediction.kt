package com.android.dreamguard.ui.prediction

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.dreamguard.R
import com.android.dreamguard.data.local.datastore.PredictionDataStore
import com.android.dreamguard.ui.main.MainActivity
import com.capstone.dreamguard.databinding.ActivityPredictionBinding
import com.android.dreamguard.utils.PredictionModelManager
import java.lang.Exception

class ActivityPrediction : AppCompatActivity() {

    private lateinit var binding: ActivityPredictionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPredictionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectGender(PredictionDataStore.gender ?: "male")

        setupToolbar()
        setupListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun setupListeners() {
        binding.cardButtonMale.setOnClickListener {
            selectGender("male")
        }

        binding.cardButtonFemale.setOnClickListener {
            selectGender("female")
        }

        binding.predictButton.setOnClickListener {
            proceedToNextStep()
        }
    }

    private fun selectGender(gender: String) {
        PredictionDataStore.gender = gender

        when (gender) {
            "male" -> {
                binding.cardButtonMale.backgroundTintList =
                    getColorStateList(R.color.purple)
                binding.cardButtonMale.setTextColor(getColor(R.color.white))
                binding.cardButtonFemale.backgroundTintList =
                    getColorStateList(R.color.white)
                binding.cardButtonFemale.setTextColor(getColor(R.color.gray))
            }
            "female" -> {
                binding.cardButtonFemale.backgroundTintList =
                    getColorStateList(R.color.purple)
                binding.cardButtonFemale.setTextColor(getColor(R.color.white))
                binding.cardButtonMale.backgroundTintList =
                    getColorStateList(R.color.white)
                binding.cardButtonMale.setTextColor(getColor(R.color.gray))
            }
        }
    }

    private fun proceedToNextStep() {
        if (PredictionDataStore.gender.isNullOrEmpty()) {
            println("Gender is required")
            return
        }
        val intent = Intent(this, ActivityPrediction2::class.java)
        startActivity(intent)
    }
}
