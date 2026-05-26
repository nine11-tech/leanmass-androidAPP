package com.anass.leanmasscalculator.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anass.leanmasscalculator.data.session.SessionManager
import com.anass.leanmasscalculator.databinding.ActivitySplashBinding
import com.anass.leanmasscalculator.ui.auth.LoginActivity
import com.anass.leanmasscalculator.ui.home.HomeActivity
import com.anass.leanmasscalculator.util.SecureScreenHelper

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SecureScreenHelper.enableSecureMode(window)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val target = if (SessionManager(this).isSessionValid()) HomeActivity::class.java else LoginActivity::class.java
        startActivity(Intent(this, target))
        finish()
    }
}
