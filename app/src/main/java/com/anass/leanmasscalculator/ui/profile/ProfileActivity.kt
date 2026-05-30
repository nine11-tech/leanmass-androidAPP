package com.anass.leanmasscalculator.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anass.leanmasscalculator.R
import com.anass.leanmasscalculator.data.local.entity.UserEntity
import com.anass.leanmasscalculator.databinding.ActivityProfileBinding
import com.anass.leanmasscalculator.ui.auth.LoginActivity
import com.anass.leanmasscalculator.util.AppDependencies
import com.anass.leanmasscalculator.util.SecureScreenHelper
import com.anass.leanmasscalculator.util.SecurityChecks

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SecureScreenHelper.enableSecureMode(window)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logoutButton.setOnClickListener { logout() }
    }

    override fun onResume() {
        super.onResume()
        val authRepository = AppDependencies.authRepository(this)
        val user = authRepository.currentUser()
        if (user == null) {
            logout()
            return
        }

        bindProfile(user)
    }

    private fun bindProfile(user: UserEntity) {
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
        showSecurityStatus()
    }

    private fun showSecurityStatus() {
        val status = SecurityChecks.getSecurityStatus(this)
        binding.securityStatusText.text = getString(
            if (status.isNormal) R.string.security_status_normal else R.string.security_status_warning
        )
        binding.securityStatusDetails.text = getString(
            R.string.security_status_details,
            yesNo(status.rooted),
            yesNo(status.debuggerAttached),
            yesNo(status.appDebuggable)
        )
    }

    private fun yesNo(value: Boolean): String = getString(if (value) R.string.yes else R.string.no)

    private fun logout() {
        AppDependencies.sessionManager(this).clearSession()
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}
