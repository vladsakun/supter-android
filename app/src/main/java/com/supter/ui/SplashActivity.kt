package com.supter.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.supter.ui.auth.LoginActivity
import com.supter.ui.main.MainActivity
import com.supter.utils.SystemUtils

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        if (SystemUtils.getToken(applicationContext).length > 10) {
            startMainActivity()
        } else {
            startLoginActivity()
        }

    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun startLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

}

