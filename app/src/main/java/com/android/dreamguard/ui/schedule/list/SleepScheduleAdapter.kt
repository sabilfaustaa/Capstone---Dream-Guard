package com.android.dreamguard.ui.schedule.list

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.dreamguard.R
import com.capstone.dreamguard.databinding.ItemSleepScheduleBinding
import com.android.dreamguard.data.remote.models.SleepSchedule
import com.android.dreamguard.ui.schedule.add.AddScheduleActivity

class SleepScheduleAdapter(
    private val onEditClick: (SleepSchedule) -> Unit,
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
            // Planned Data
            binding.labelPlannedText.text = binding.root.context.getString(R.string.label_planned)
            binding.labelClockWake.text = schedule.wakeUpTime ?: "N/A"
            binding.labelClockBed.text = schedule.bedTime ?: "N/A"
            binding.labelClockDuration.text = schedule.plannedDuration ?: "N/A"
            binding.cardContentText.text = schedule.createdAt ?: "-"

            if (schedule.actualBedTime.isNullOrEmpty() || schedule.actualWakeUpTime.isNullOrEmpty()) {
                binding.actualButton.visibility = View.VISIBLE
                binding.linearActual.visibility = View.GONE

                binding.labelClockDurationActual.text = "--"
                binding.labelClockWakeTimeActual.text = "--"
                binding.labelClockDurationActual2.text = "--"
                binding.labelClockDifferentTimeActual.text = "--"
                binding.labelNotesDesc.text = "--"
                binding.labelSleepQualityDesc.text = "--"

                binding.actualButton.setOnClickListener {
                    onActualDataClick(schedule)
                }
            } else {
                binding.actualButton.visibility = View.GONE
                binding.linearActual.visibility = View.VISIBLE

                binding.labelClockDurationActual.text = schedule.actualBedTime
                binding.labelClockWakeTimeActual.text = schedule.actualWakeUpTime
                binding.labelClockDurationActual2.text = schedule.actualDuration ?: "N/A"
                binding.labelClockDifferentTimeActual.text = schedule.difference ?: "N/A"
                binding.labelNotesDesc.text = schedule.notes ?: "No notes available"
                binding.labelSleepQualityDesc.text = schedule.sleepQuality ?: "Not rated"
            }

            binding.editButton.setOnClickListener {
                onEditClick(schedule)
            }
        }
    }
}
