package com.android.dreamguard.ui.schedule.actual

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.android.dreamguard.data.remote.models.SleepScheduleRequest
import com.android.dreamguard.data.remote.models.SleepSchedule
import com.android.dreamguard.ui.schedule.list.SleepSchedulerActivity
import com.capstone.dreamguard.R
import com.capstone.dreamguard.databinding.ActivityActualDataBinding
import java.text.SimpleDateFormat
import java.util.*

class ActualDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActualDataBinding
    private val viewModel: ActualDataViewModel by viewModels {
        ActualDataViewModelFactory(this)
    }
    private var schedule: SleepSchedule? = null
    private var selectedQuality: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActualDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        schedule = intent.getParcelableExtra("schedule_data")
        setupToolbar()
        setupUI()
        setupActions()
        setupObservers()

        binding.currentEditText.doAfterTextChanged { text ->
            if (text != null && text.length > 100) {
                binding.notesEditTextLayout.error = "Maksimal 100 karakter"
            } else {
                binding.notesEditTextLayout.error = null
            }
        }

    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupUI() {
        schedule?.let {
            binding.buttonBedtime.text = it.actualBedTime ?: "Set Bedtime"
            binding.buttonWakeUp.text = it.actualWakeUpTime ?: "Set Wakeup"
            binding.currentEditText.setText(it.notes ?: "")
            selectedQuality = it.sleepQuality?.toIntOrNull()
            updateSleepQualitySelection(selectedQuality)
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

        // Selector Actions
        setupSleepQualitySelector()

        binding.goalButton.setOnClickListener {
            updateActualData()
        }
    }

    private fun setupSleepQualitySelector() {
        val qualitySelectors = mapOf(
            binding.linearQuality10 to 10,
            binding.linearQuality9 to 9,
            binding.linearQuality8 to 8,
            binding.linearQuality7 to 7,
            binding.linearQuality6 to 6,
            binding.linearQuality5 to 5,
            binding.linearQuality4 to 4,
            binding.linearQuality3 to 3,
            binding.linearQuality2 to 2,
            binding.linearQuality1 to 1
        )

        qualitySelectors.forEach { (view, quality) ->
            view.setOnClickListener {
                selectedQuality = quality
                updateSleepQualitySelection(quality)
            }
        }
    }

    private fun updateSleepQualitySelection(selectedQuality: Int?) {
        val qualitySelectors = mapOf(
            10 to binding.linearQuality10.findViewById(R.id.imageQuality10),
            9 to binding.linearQuality9.findViewById(R.id.imageQuality9),
            8 to binding.linearQuality8.findViewById(R.id.imageQuality8),
            7 to binding.linearQuality7.findViewById(R.id.imageQuality7),
            6 to binding.linearQuality6.findViewById(R.id.imageQuality6),
            5 to binding.linearQuality5.findViewById(R.id.imageQuality5),
            4 to binding.linearQuality4.findViewById(R.id.imageQuality4),
            3 to binding.linearQuality3.findViewById(R.id.imageQuality3),
            2 to binding.linearQuality2.findViewById(R.id.imageQuality2),
            1 to binding.linearQuality1.findViewById<ImageView>(R.id.imageQuality1)
        )


        qualitySelectors.values.forEach { imageView ->
            imageView?.setImageResource(R.drawable.qual_teal)
        }

        selectedQuality?.let {
            qualitySelectors[it]?.setImageResource(R.drawable.qual_purple)
        }
    }


    private fun setupObservers() {
        viewModel.updateResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Actual data updated successfully", Toast.LENGTH_SHORT).show()
                navigateToSleepScheduleList()
            } else {
                Toast.makeText(this, "Failed to update actual data", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            Toast.makeText(this, error ?: "An error occurred", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateActualData() {
        val actualBedTime = binding.buttonBedtime.text.toString()
        val actualWakeUpTime = binding.buttonWakeUp.text.toString()
        val notes = binding.currentEditText.text.toString()
        val quality = selectedQuality?.toString()

        if (!validateInputs(actualBedTime, actualWakeUpTime, quality)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        schedule?.let {
            viewModel.updateSleepSchedule(
                scheduleId = it.id,
                actualBedTime = actualBedTime,
                actualWakeUpTime = actualWakeUpTime,
                sleepQuality = quality,
                notes = notes
            )
        }
    }

    private fun validateInputs(bedTime: String, wakeUpTime: String, quality: String?): Boolean {
        return bedTime != "Set Bedtime" && wakeUpTime != "Set Wakeup" && quality != null
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
