package com.android.dreamguard.ui.schedule.goal

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.dreamguard.ui.utils.NumberPickerDialog
import com.capstone.dreamguard.R
import com.capstone.dreamguard.databinding.ActivitySleepGoalBinding
import java.util.Locale

class SleepGoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySleepGoalBinding
    private val viewModel: SleepGoalViewModel by viewModels {
        SleepGoalViewModelFactory(this)
    }

    private var sleepGoal: String = "08:30"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySleepGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupObservers()
        setupActions()
    }

    private fun setupObservers() {
        viewModel.sleepGoal.observe(this) { goal ->
            if (goal != null) {
                sleepGoal = goal
                updateSleepGoalUI(goal)
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateSleepGoalUI(goal: String) {
        val timeParts = goal.split(":")
        val hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()

        binding.textHours.text = hour.toString().padStart(2, '0')
        binding.textMinutes.text = minute.toString().padStart(2, '0')
    }

    private fun setupActions() {
        binding.cardHours.setOnClickListener {
            val currentHour = sleepGoal.split(":")[0].toInt()
            val currentMinute = sleepGoal.split(":")[1].toInt()

            NumberPickerDialog(
                this,
                "Set Sleep Goal",
                currentHour,
                currentMinute
            ) { hour, minute ->
                val updatedSleepGoal = String.format("%02d:%02d", hour, minute)
                viewModel.updateSleepGoal(updatedSleepGoal)
            }.show()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }
}

