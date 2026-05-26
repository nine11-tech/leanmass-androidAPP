package com.anass.leanmasscalculator.ui.history

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.anass.leanmasscalculator.data.local.entity.CalculationEntity
import com.anass.leanmasscalculator.databinding.ActivityHistoryBinding
import com.anass.leanmasscalculator.util.AppDependencies
import com.google.android.material.snackbar.Snackbar

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: HistoryAdapter
    private val userId: Long by lazy { AppDependencies.sessionManager(this).getUserId() ?: -1L }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = HistoryAdapter { confirmDelete(it) }
        binding.historyRecycler.adapter = adapter
        binding.clearButton.setOnClickListener { confirmClear() }
        loadHistory()
    }

    private fun loadHistory() {
        val history = AppDependencies.calculationRepository(this).history(userId)
        adapter.submitList(history)
        binding.emptyText.visibility = if (history.isEmpty()) View.VISIBLE else View.GONE
        binding.historyRecycler.visibility = if (history.isEmpty()) View.GONE else View.VISIBLE
        binding.clearButton.isEnabled = history.isNotEmpty()
    }

    private fun confirmDelete(item: CalculationEntity) {
        AlertDialog.Builder(this)
            .setTitle("Delete calculation?")
            .setMessage("This history item will be removed permanently.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                AppDependencies.calculationRepository(this).delete(item.id, userId)
                loadHistory()
                Snackbar.make(binding.root, "Calculation deleted.", Snackbar.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun confirmClear() {
        AlertDialog.Builder(this)
            .setTitle("Clear all history?")
            .setMessage("All saved calculations for this account will be deleted.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Clear") { _, _ ->
                AppDependencies.calculationRepository(this).clear(userId)
                loadHistory()
                Snackbar.make(binding.root, "History cleared.", Snackbar.LENGTH_SHORT).show()
            }
            .show()
    }
}
