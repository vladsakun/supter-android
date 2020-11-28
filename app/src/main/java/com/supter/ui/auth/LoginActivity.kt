package com.supter.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.supter.R
import com.supter.databinding.LoginActivityBinding
import com.supter.ui.ScopedActivity
import com.supter.ui.auth.login.LoginFragment
import com.supter.ui.auth.signup.SignUpFragment

class LoginActivity : ScopedActivity() {

    lateinit var mBinding: LoginActivityBinding
    private var isLoginFragmentActive = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = LoginActivityBinding.inflate(layoutInflater)
        val view = mBinding.root
        setContentView(view)
        setSupportActionBar(mBinding.toolbar.toolbar as Toolbar)

        presentFragment(LoginFragment(), "LoginFragment")
    }

    fun presentFragment(fragment: Fragment, tag: String) {

        if (fragment is SignUpFragment) {
            isLoginFragmentActive = false
        }

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            .addToBackStack(tag)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onBackPressed() {
        if (isLoginFragmentActive) {
            finish()
        } else {
            isLoginFragmentActive = true
            super.onBackPressed()
        }
    }

    companion object {
        fun getStartIntent(context: Context): Intent {
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            return intent
        }
    }
}