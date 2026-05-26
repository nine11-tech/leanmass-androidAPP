package com.anass.leanmasscalculator.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.anass.leanmasscalculator.R
import com.anass.leanmasscalculator.core.model.Gender
import com.anass.leanmasscalculator.core.validation.CalculatorInputValidator
import com.anass.leanmasscalculator.data.local.entity.CalculationEntity
import com.anass.leanmasscalculator.data.local.entity.UserEntity
import com.anass.leanmasscalculator.databinding.ActivityHomeBinding
import com.anass.leanmasscalculator.ui.auth.LoginActivity
import com.anass.leanmasscalculator.ui.history.HistoryActivity
import com.anass.leanmasscalculator.ui.profile.ProfileActivity
import com.anass.leanmasscalculator.util.AppDependencies
import com.google.android.material.snackbar.Snackbar

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var user: UserEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = AppDependencies.authRepository(this).currentUser()
        if (user == null) {
            redirectToLogin()
            return
        }

        binding.welcomeText.text = "Hi, ${user!!.fullName}"
        binding.calculateButton.setOnClickListener { calculate() }
        binding.historyButton.setOnClickListener { startActivity(Intent(this, HistoryActivity::class.java)) }
        binding.profileButton.setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }
    }

    override fun onResume() {
        super.onResume()
        user?.let { refreshStats(it.id) }
    }

    private fun calculate() {
        clearErrors()
        val weight = CalculatorInputValidator.parsePositiveDouble(binding.weightInput.text?.toString().orEmpty())
        val height = CalculatorInputValidator.parsePositiveDouble(binding.heightInput.text?.toString().orEmpty())

        if (weight == null) {
            binding.weightLayout.error = "Enter a valid weight."
            return
        }
        if (height == null) {
            binding.heightLayout.error = "Enter a valid height."
            return
        }

        val gender = if (binding.genderToggle.checkedButtonId == R.id.femaleButton) Gender.FEMALE else Gender.MALE
        val calculation = AppDependencies.calculationRepository(this)
            .calculateAndSave(user!!.id, weight, height, gender)
        showResult(calculation)
        refreshStats(user!!.id)
        Snackbar.make(binding.root, "Calculation saved to history.", Snackbar.LENGTH_SHORT).show()
    }

    private fun showResult(calculation: CalculationEntity) {
        binding.resultCard.visibility = View.VISIBLE
        binding.resultValueText.text = String.format("%.1f kg", calculation.lbmKg)
        binding.resultMessageText.text = calculation.message
        val color = if (calculation.isSatisfactory) R.color.success else R.color.warning
        val container = if (calculation.isSatisfactory) R.color.success_container else R.color.warning_container
        val icon = if (calculation.isSatisfactory) R.drawable.ic_good else R.drawable.ic_warning
        binding.resultMessageText.setTextColor(ContextCompat.getColor(this, color))
        binding.resultIcon.setImageResource(icon)
        binding.resultCard.setCardBackgroundColor(ContextCompat.getColor(this, container))
    }

    private fun refreshStats(userId: Long) {
        val stats = AppDependencies.calculationRepository(this).stats(userId)
        binding.totalText.text = stats.total.toString()
        binding.averageText.text = stats.averageLbm?.let { String.format("%.1f kg", it) } ?: "-- kg"
        binding.lastLbmText.text = stats.last?.let { String.format("%.1f kg", it.lbmKg) } ?: "-- kg"
    }

    private fun clearErrors() {
        binding.weightLayout.error = null
        binding.heightLayout.error = null
    }

    private fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}
