package com.android.dreamguard.ui.result

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.dreamguard.ui.main.MainActivity
import com.capstone.dreamguard.databinding.ActivityResultNoSleepDisorderUnderStressBinding

class ActivityResultNoSleepDisorderUnderStress : AppCompatActivity() {

    private lateinit var binding: ActivityResultNoSleepDisorderUnderStressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultNoSleepDisorderUnderStressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
//        binding.predictText.text = "Sleep Apnea Detected"
//        binding.textResult.text = "Your results indicate sleep apnea. Consult a healthcare provider for appropriate guidance."
    }

    private fun setupListeners() {
        binding.iconButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
