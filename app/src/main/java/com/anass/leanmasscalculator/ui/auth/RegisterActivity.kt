package com.anass.leanmasscalculator.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anass.leanmasscalculator.data.repository.AuthResult
import com.anass.leanmasscalculator.databinding.ActivityRegisterBinding
import com.anass.leanmasscalculator.ui.home.HomeActivity
import com.anass.leanmasscalculator.util.AppDependencies
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createButton.setOnClickListener { register() }
        binding.loginLink.setOnClickListener { finish() }
    }

    private fun register() {
        clearErrors()
        val result = AppDependencies.authRepository(this).register(
            fullName = binding.fullNameInput.text?.toString().orEmpty(),
            email = binding.emailInput.text?.toString().orEmpty(),
            password = binding.passwordInput.text?.toString().orEmpty(),
            confirmPassword = binding.confirmPasswordInput.text?.toString().orEmpty()
        )
        when (result) {
            is AuthResult.Success -> {
                startActivity(Intent(this, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }
            is AuthResult.Error -> {
                assignError(result.message)
                Snackbar.make(binding.root, result.message, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun assignError(message: String) {
        when {
            message.contains("name", ignoreCase = true) -> binding.fullNameLayout.error = message
            message.contains("email", ignoreCase = true) || message.contains("account", ignoreCase = true) -> binding.emailLayout.error = message
            message.contains("match", ignoreCase = true) -> binding.confirmPasswordLayout.error = message
            else -> binding.passwordLayout.error = message
        }
    }

    private fun clearErrors() {
        binding.fullNameLayout.error = null
        binding.emailLayout.error = null
        binding.passwordLayout.error = null
        binding.confirmPasswordLayout.error = null
    }
}
