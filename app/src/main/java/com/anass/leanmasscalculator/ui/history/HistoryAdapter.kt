package com.anass.leanmasscalculator.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.anass.leanmasscalculator.R
import com.anass.leanmasscalculator.core.model.Gender
import com.anass.leanmasscalculator.data.local.entity.CalculationEntity
import com.anass.leanmasscalculator.databinding.ItemHistoryBinding
import com.anass.leanmasscalculator.util.DateFormatter

class HistoryAdapter(
    private val onDelete: (CalculationEntity) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    private val items = mutableListOf<CalculationEntity>()

    fun submitList(newItems: List<CalculationEntity>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CalculationEntity) {
            val context = binding.root.context
            binding.lbmText.text = context.getString(R.string.kg_value, item.lbmKg)
            binding.statusText.text = item.message
            binding.statusText.setTextColor(
                ContextCompat.getColor(context, if (item.isSatisfactory) R.color.success else R.color.warning)
            )
            val gender = if (item.gender == Gender.MALE) {
                context.getString(R.string.male)
            } else {
                context.getString(R.string.female)
            }
            binding.detailsText.text = context.getString(
                R.string.history_item_details,
                gender,
                item.weightKg,
                item.heightCm
            )
            binding.dateText.text = DateFormatter.format(item.createdAt)
            binding.deleteButton.setOnClickListener { onDelete(item) }
        }
    }
}
