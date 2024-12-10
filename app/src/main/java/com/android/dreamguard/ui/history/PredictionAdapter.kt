package com.android.dreamguard.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.dreamguard.data.remote.models.PredictionItem
import com.capstone.dreamguard.databinding.ItemHistoryPredictionBinding

class PredictionAdapter(
    private var predictions: List<PredictionItem>
) : RecyclerView.Adapter<PredictionAdapter.PredictionViewHolder>() {

    inner class PredictionViewHolder(private val binding: ItemHistoryPredictionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PredictionItem) {
            binding.cardContentText.text = item.createdAt
            binding.resultPrediction.text = item.predictionResultText
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredictionViewHolder {
        val binding = ItemHistoryPredictionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PredictionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PredictionViewHolder, position: Int) {
        holder.bind(predictions[position])
    }

    override fun getItemCount(): Int = predictions.size

    fun updateData(newPredictions: List<PredictionItem>) {
        predictions = newPredictions
        notifyDataSetChanged()
    }
}
