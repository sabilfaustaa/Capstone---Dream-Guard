package com.android.dreamguard.ui.schedule.add

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.dreamguard.ui.schedule.list.SleepSchedulerActivity
import com.capstone.dreamguard.databinding.ActivityNewScheduleBinding
import android.app.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewScheduleBinding
    private val viewModel: AddScheduleViewModel by viewModels {
        AddScheduleViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupObservers()
        setupActions()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.addScheduleResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Schedule added successfully", Toast.LENGTH_SHORT).show()
                navigateToSleepScheduleList()
                finish()
            } else {
                Toast.makeText(this, "Failed to add schedule", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            Log.d("TESTADD", error)
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupActions() {
        binding.buttonBedtime.setOnClickListener {
            showTimePicker { time ->
                binding.buttonBedtime.text = time
            }
        }

        binding.buttonWakeUp.setOnClickListener {
            showTimePicker { time ->
                binding.buttonWakeUp.text = time
            }
        }


        binding.goalButton.setOnClickListener {
            val bedTime = binding.buttonBedtime.text.toString()
            val wakeUpTime = binding.buttonWakeUp.text.toString()
            val wakeUpAlarm = binding.toggleWakeUp.isChecked
            val sleepReminders = binding.toggleSleepReminder.isChecked

            if (validateInputs(bedTime, wakeUpTime)) {
                viewModel.addSleepSchedule(bedTime, wakeUpTime, wakeUpAlarm, sleepReminders)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun validateInputs(bedTime: String, wakeUpTime: String): Boolean {
        return bedTime != "Set Time" && wakeUpTime != "Set Time"
    }

    private fun navigateToSleepScheduleList() {
        val intent = Intent(this, SleepSchedulerActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showTimePicker(onTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val formattedTime = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
                    .format(Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, selectedHour)
                        set(Calendar.MINUTE, selectedMinute)
                    }.time)
                onTimeSelected(formattedTime)
            },
            hour,
            minute,
            false
        ).show()
    }

}
