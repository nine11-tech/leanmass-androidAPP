package com.anass.leanmasscalculator.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anass.leanmasscalculator.data.repository.AuthResult
import com.anass.leanmasscalculator.databinding.ActivityLoginBinding
import com.anass.leanmasscalculator.ui.home.HomeActivity
import com.anass.leanmasscalculator.util.AppDependencies
import com.anass.leanmasscalculator.util.SecureScreenHelper
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SecureScreenHelper.enableSecureMode(window)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener { login() }
        binding.registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login() {
        clearErrors()
        val email = binding.emailInput.text?.toString().orEmpty()
        val password = binding.passwordInput.text?.toString().orEmpty()
        when (val result = AppDependencies.authRepository(this).login(email, password)) {
            is AuthResult.Success -> {
                startActivity(Intent(this, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }
            is AuthResult.Error -> {
                binding.passwordLayout.error = result.message
                Snackbar.make(binding.root, result.message, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun clearErrors() {
        binding.emailLayout.error = null
        binding.passwordLayout.error = null
    }
}
