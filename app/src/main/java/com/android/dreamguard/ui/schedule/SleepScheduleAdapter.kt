package com.android.dreamguard.ui.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.dreamguard.R
import com.capstone.dreamguard.databinding.ItemSleepScheduleBinding
import com.android.dreamguard.data.remote.models.SleepSchedule

class SleepScheduleAdapter(
    private val onActualDataClick: (SleepSchedule) -> Unit
) : RecyclerView.Adapter<SleepScheduleAdapter.SleepScheduleViewHolder>() {

    private val items = mutableListOf<SleepSchedule>()

    fun updateData(newItems: List<SleepSchedule>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SleepScheduleViewHolder {
        val binding = ItemSleepScheduleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SleepScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SleepScheduleViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class SleepScheduleViewHolder(private val binding: ItemSleepScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(schedule: SleepSchedule) {
            binding.labelPlannedText.text = binding.root.context.getString(R.string.label_planned)
            binding.labelBedtime.text = schedule.bedTime ?: "N/A"
            binding.labelWake.text = schedule.wakeUpTime ?: "N/A"
            binding.labelDuration.text = schedule.plannedDuration ?: "N/A"

            if (schedule.actualBedTime.isNullOrEmpty() || schedule.actualWakeUpTime.isNullOrEmpty()) {
                binding.actualButton.visibility = View.VISIBLE
                binding.linearActual.visibility = View.GONE
                binding.actualButton.setOnClickListener {
                    onActualDataClick(schedule)
                }
            } else {
                binding.actualButton.visibility = View.GONE
                binding.linearActual.visibility = View.VISIBLE
                binding.labelBedActual.text = schedule.actualBedTime
                binding.labelWakeActual.text = schedule.actualWakeUpTime
                binding.labelDurationActual.text = schedule.actualDuration ?: "N/A"
                binding.labelDifferenceActual.text = schedule.difference ?: "N/A"
            }
        }
    }
}
