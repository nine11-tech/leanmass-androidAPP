package com.anass.leanmasscalculator.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anass.leanmasscalculator.R
import com.anass.leanmasscalculator.databinding.ActivityProfileBinding
import com.anass.leanmasscalculator.ui.auth.LoginActivity
import com.anass.leanmasscalculator.util.AppDependencies

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val authRepository = AppDependencies.authRepository(this)
        val user = authRepository.currentUser()
        if (user == null) {
            logout()
            return
        }

        binding.nameText.text = user.fullName
        binding.emailText.text = user.email
        val stats = AppDependencies.calculationRepository(this).stats(user.id)
        binding.totalText.text = getString(R.string.total_calculations, stats.total)
        binding.averageText.text = getString(
            R.string.average_lbm,
            stats.averageLbm?.let { getString(R.string.kg_value, it) } ?: getString(R.string.empty_lbm)
        )
        binding.lastText.text = getString(
            R.string.last_lbm,
            stats.last?.let { getString(R.string.kg_value, it.lbmKg) } ?: getString(R.string.empty_lbm)
        )
        binding.logoutButton.setOnClickListener { logout() }
    }

    private fun logout() {
        AppDependencies.sessionManager(this).clear()
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}
