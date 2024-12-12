package com.android.dreamguard.ui.schedule.goal

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.dreamguard.ui.schedule.list.SleepSchedulerActivity
import com.android.dreamguard.ui.utils.NumberPickerDialog
import com.capstone.dreamguard.R
import com.capstone.dreamguard.databinding.ActivitySleepGoalBinding

class SleepGoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySleepGoalBinding
    private val viewModel: SleepGoalViewModel by viewModels {
        SleepGoalViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySleepGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupObservers()
        setupActions()

        viewModel.fetchSleepGoal()
    }

    private fun setupObservers() {
        viewModel.sleepGoal.observe(this) { goal ->
            if (goal != null) {
                updateSleepGoalUI(goal.first, goal.second)
            }
        }

        viewModel.updateSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Sleep goal updated successfully", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateSleepGoalUI(hours: Int, minutes: Int) {
        binding.textHours.text = hours.toString().padStart(2, '0')
        binding.textMinutes.text = minutes.toString().padStart(2, '0')
    }

    private fun setupActions() {
        binding.goalButton.setOnClickListener {
            val currentHours = binding.textHours.text.toString().toInt()
            val currentMinutes = binding.textMinutes.text.toString().toInt()

            NumberPickerDialog(
                this,
                "Set Sleep Goal",
                currentHours,
                currentMinutes
            ) { hours, minutes ->
                viewModel.updateSleepGoal(hours, minutes)
                navigateTo()
            }.show()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            navigateTo()
        }
    }

    private fun navigateTo() {
        val intent = Intent(this, SleepSchedulerActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }
}
