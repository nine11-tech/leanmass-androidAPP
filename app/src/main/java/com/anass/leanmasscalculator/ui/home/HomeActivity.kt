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
import com.anass.leanmasscalculator.util.SecureScreenHelper
import com.google.android.material.snackbar.Snackbar

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var user: UserEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SecureScreenHelper.enableSecureMode(window)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = AppDependencies.authRepository(this).currentUser()
        if (user == null) {
            redirectToLogin()
            return
        }

        binding.welcomeText.text = getString(R.string.welcome_user, user!!.fullName)
        binding.calculateButton.setOnClickListener { calculate() }
        binding.historyButton.setOnClickListener { startActivity(Intent(this, HistoryActivity::class.java)) }
        binding.profileButton.setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }
    }

    override fun onResume() {
        super.onResume()
        user = AppDependencies.authRepository(this).currentUser()
        val currentUser = user
        if (currentUser == null) {
            redirectToLogin()
            return
        }
        refreshStats(currentUser.id)
    }

    private fun calculate() {
        clearErrors()
        val weight = CalculatorInputValidator.parsePositiveDouble(binding.weightInput.text?.toString().orEmpty())
        val height = CalculatorInputValidator.parsePositiveDouble(binding.heightInput.text?.toString().orEmpty())

        if (weight == null) {
            binding.weightLayout.error = getString(R.string.valid_weight_error)
            return
        }
        if (height == null) {
            binding.heightLayout.error = getString(R.string.valid_height_error)
            return
        }

        val gender = if (binding.genderToggle.checkedButtonId == R.id.femaleButton) Gender.FEMALE else Gender.MALE
        val currentUser = user ?: run {
            redirectToLogin()
            return
        }
        val calculation = AppDependencies.calculationRepository(this)
            .calculateAndSave(currentUser.id, weight, height, gender)
        showResult(calculation)
        refreshStats(currentUser.id)
        Snackbar.make(binding.root, getString(R.string.calculation_saved), Snackbar.LENGTH_SHORT).show()
    }

    private fun showResult(calculation: CalculationEntity) {
        binding.resultCard.visibility = View.VISIBLE
        binding.resultValueText.text = getString(R.string.kg_value, calculation.lbmKg)
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
        binding.averageText.text = stats.averageLbm?.let { getString(R.string.kg_value, it) } ?: getString(R.string.empty_lbm)
        binding.lastLbmText.text = stats.last?.let { getString(R.string.kg_value, it.lbmKg) } ?: getString(R.string.empty_lbm)
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
