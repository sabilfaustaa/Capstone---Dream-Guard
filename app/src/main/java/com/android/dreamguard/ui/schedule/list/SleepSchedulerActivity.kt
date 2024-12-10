package com.android.dreamguard.ui.schedule.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.dreamguard.ui.history.PredictionHistoryActivity
import com.android.dreamguard.ui.schedule.add.AddScheduleActivity
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

        binding.floatingActionButton.setOnClickListener({
            navigateToAddScheduler()
        })

        setupRecyclerView()
        setupObservers()
        viewModel.fetchSleepSchedules()
    }

    private fun setupRecyclerView() {
        adapter = SleepScheduleAdapter(
            onActualDataClick = { schedule ->
                val intent = Intent(this, AddScheduleActivity::class.java)
                intent.putExtra("schedule_data", schedule)
                intent.putExtra("isEdit", true)
                startActivity(intent)
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
            Log.d("GETSCHEDULLER", error)
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToAddScheduler() {
        val intent = Intent(this, AddScheduleActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }
}
