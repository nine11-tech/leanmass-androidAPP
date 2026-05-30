package com.anass.leanmasscalculator.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.anass.leanmasscalculator.R
import com.anass.leanmasscalculator.data.local.entity.CalculationEntity
import com.anass.leanmasscalculator.databinding.ActivityHistoryBinding
import com.anass.leanmasscalculator.ui.auth.LoginActivity
import com.anass.leanmasscalculator.util.AppDependencies
import com.anass.leanmasscalculator.util.SecureScreenHelper
import com.google.android.material.snackbar.Snackbar

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: HistoryAdapter
    private var userId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SecureScreenHelper.enableSecureMode(window)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = HistoryAdapter { confirmDelete(it) }
        binding.historyRecycler.adapter = adapter
        binding.clearButton.setOnClickListener { confirmClear() }
    }

    override fun onResume() {
        super.onResume()
        val user = AppDependencies.authRepository(this).currentUser()
        if (user == null) {
            redirectToLogin()
            return
        }
        userId = user.id
        loadHistory()
    }

    private fun loadHistory() {
        val currentUserId = userId ?: return
        val history = AppDependencies.calculationRepository(this).history(currentUserId)
        adapter.submitList(history)
        binding.emptyText.visibility = if (history.isEmpty()) View.VISIBLE else View.GONE
        binding.historyRecycler.visibility = if (history.isEmpty()) View.GONE else View.VISIBLE
        binding.clearButton.isEnabled = history.isNotEmpty()
    }

    private fun confirmDelete(item: CalculationEntity) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_calculation_title)
            .setMessage(R.string.delete_calculation_message)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                val currentUserId = userId ?: return@setPositiveButton
                AppDependencies.calculationRepository(this).delete(item.id, currentUserId)
                loadHistory()
                Snackbar.make(binding.root, getString(R.string.calculation_deleted), Snackbar.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun confirmClear() {
        AlertDialog.Builder(this)
            .setTitle(R.string.clear_history_title)
            .setMessage(R.string.clear_history_message)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.clear) { _, _ ->
                val currentUserId = userId ?: return@setPositiveButton
                AppDependencies.calculationRepository(this).clear(currentUserId)
                loadHistory()
                Snackbar.make(binding.root, getString(R.string.history_cleared), Snackbar.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}
