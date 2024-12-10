package com.android.dreamguard.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.dreamguard.data.remote.models.PredictionItem
import com.capstone.dreamguard.databinding.ItemHistoryPredictionBinding

class PredictionAdapter(
    private var predictions: MutableList<PredictionItem>,
    private val onItemButtonClicked: (predictionId: Int) -> Unit
) : RecyclerView.Adapter<PredictionAdapter.PredictionViewHolder>() {

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

    inner class PredictionViewHolder(private val binding: ItemHistoryPredictionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(prediction: PredictionItem) {
            binding.cardContentText.text = prediction.createdAt
            binding.resultPrediction.text = prediction.predictionResultText

            // Atur tombol berdasarkan predictionId
            binding.btnRecommendation.setOnClickListener {
                onItemButtonClicked(prediction.predictionResultId)
            }
        }
    }

    fun updateData(newPredictions: List<PredictionItem>) {
        predictions.clear()
        predictions.addAll(newPredictions)
        notifyDataSetChanged()
    }
}
