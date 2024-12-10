package com.android.dreamguard.ui.schedule

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.dreamguard.data.remote.models.SleepSchedule
import com.capstone.dreamguard.databinding.ActivitySleepSchedulerBinding

class SleepSchedulerActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySleepSchedulerBinding
    private val viewModel: SleepScheduleViewModel by viewModels {
        SleepScheduleViewModelFactory(this)
    }
    private lateinit var adapter: SleepScheduleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySleepSchedulerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        viewModel.fetchSleepSchedules()
    }

    private fun setupRecyclerView() {
        adapter = SleepScheduleAdapter(
            onActualDataClick = { schedule ->
                Toast.makeText(this, "Actual Data for ${schedule.id} clicked", Toast.LENGTH_SHORT).show()
                // Handle further actions for actual data click
            }
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SleepSchedulerActivity)
            adapter = this@SleepSchedulerActivity.adapter
        }
    }

    private fun setupObservers() {
        viewModel.sleepSchedules.observe(this) { result ->
            if (result != null) {
                adapter.updateData(result)
            } else {
                Toast.makeText(this, "Failed to fetch schedules", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
