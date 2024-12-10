package com.android.dreamguard.ui.schedule.add

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.dreamguard.data.remote.models.SleepSchedule
import com.android.dreamguard.data.remote.models.SleepScheduleRequest
import com.android.dreamguard.ui.schedule.list.SleepSchedulerActivity
import com.capstone.dreamguard.R
import com.capstone.dreamguard.databinding.ActivityNewScheduleBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewScheduleBinding
    private val viewModel: AddScheduleViewModel by viewModels {
        AddScheduleViewModelFactory(this)
    }
    private var isEdit: Boolean = false
    private var schedule: SleepSchedule? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isEdit = intent.getBooleanExtra("isEdit", false)
        schedule = intent.getParcelableExtra("schedule_data")

        setupToolbar()
        setupUI()
        setupObservers()
        setupActions()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupUI() {
        if (isEdit) {
            binding.goalButton.text = getString(R.string.button_edit_schedule)
            schedule?.let {
                binding.buttonBedtime.text = it.bedTime ?: "Set Time"
                binding.buttonWakeUp.text = it.wakeUpTime ?: "Set Time"
                binding.toggleWakeUp.isChecked = it.wakeUpAlarm ?: false
                binding.toggleSleepReminder.isChecked = it.sleepReminders ?: false
            }
        } else {
            binding.goalButton.text = getString(R.string.add_schedule)
        }
    }

    private fun setupObservers() {
        viewModel.addScheduleResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Schedule saved successfully", Toast.LENGTH_SHORT).show()
                navigateToSleepScheduleList()
            } else {
                Toast.makeText(this, "Failed to save schedule", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                Log.e("AddScheduleActivity", "Error: $it")
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

            if (validateInputs(bedTime, wakeUpTime)) {
                if (isEdit) {
                    updateSchedule()
                } else {
                    addSchedule()
                }
            } else {
                Toast.makeText(this, "Please set both Bedtime and Wake Up Time", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addSchedule() {
        viewModel.addSleepSchedule(
            bedTime = binding.buttonBedtime.text.toString(),
            wakeUpTime = binding.buttonWakeUp.text.toString(),
            wakeUpAlarm = binding.toggleWakeUp.isChecked,
            sleepReminders = binding.toggleSleepReminder.isChecked
        )
    }

    private fun updateSchedule() {
        schedule?.let {
            viewModel.updateSleepSchedule(
                scheduleId = it.id,
                bedTime = binding.buttonBedtime.text.toString(),
                wakeUpTime = binding.buttonWakeUp.text.toString(),
                wakeUpAlarm = binding.toggleWakeUp.isChecked,
                sleepReminders = binding.toggleSleepReminder.isChecked
            )
        }
    }

    private fun validateInputs(bedTime: String, wakeUpTime: String): Boolean {
        return bedTime != "Set Time" && wakeUpTime != "Set Time"
    }

    private fun navigateToSleepScheduleList() {
        val intent = Intent(this, SleepSchedulerActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
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
