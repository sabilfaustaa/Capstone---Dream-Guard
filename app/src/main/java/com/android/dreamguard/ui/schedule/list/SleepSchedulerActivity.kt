package com.android.dreamguard.ui.schedule.list

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.dreamguard.ui.history.PredictionHistoryActivity
import com.android.dreamguard.ui.home.HomeActivity
import com.android.dreamguard.ui.schedule.actual.ActualDataActivity
import com.android.dreamguard.ui.schedule.add.AddScheduleActivity
import com.android.dreamguard.ui.schedule.goal.SleepGoalActivity
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

        binding.toolbar.setNavigationOnClickListener {
            navigateToHome()
        }

        binding.editButton.setOnClickListener({
            navigateToSleepGoal()
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }


        setupRecyclerView()
        setupObservers()
        viewModel.fetchSleepSchedules()
        viewModel.fetchSleepGoal()
    }

    private fun setupRecyclerView() {
        adapter = SleepScheduleAdapter(
            onEditClick = { schedule ->
                val intent = Intent(this, AddScheduleActivity::class.java)
                intent.putExtra("schedule_data", schedule)
                intent.putExtra("isEdit", true)
                startActivity(intent)
            },
            onActualDataClick = { schedule ->
                val intent = Intent(this, ActualDataActivity::class.java)
                intent.putExtra("schedule_data", schedule)
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

        viewModel.sleepGoal.observe(this) { sleepGoal ->
            binding.sleepGoalDuration.text = sleepGoal
        }

        viewModel.errorMessage.observe(this) { error ->
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

    private fun navigateToSleepGoal() {
        val intent = Intent(this, SleepGoalActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
