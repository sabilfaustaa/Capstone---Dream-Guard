package com.android.dreamguard.ui.result

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.dreamguard.ui.history.PredictionHistoryActivity
import com.android.dreamguard.ui.main.MainActivity
import com.capstone.dreamguard.databinding.ActivityResultNoSleepDisorderStressBinding

class ActivityResultNoSleepDisorderStress : AppCompatActivity() {

    private lateinit var binding: ActivityResultNoSleepDisorderStressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultNoSleepDisorderStressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.iconButton.setOnClickListener {
            val intent = Intent(this, PredictionHistoryActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
