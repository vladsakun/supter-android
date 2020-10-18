package com.supter.ui.auth

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.supter.R
import com.supter.databinding.LoginActivityBinding
import com.supter.ui.ScopedActivity
import com.supter.ui.auth.login.LoginFragment

class LoginActivity : ScopedActivity() {

    lateinit var mBinding: LoginActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.login_activity)
        setSupportActionBar(mBinding.toolbar as Toolbar)

        presentFragment(LoginFragment(), "LoginFragment")
    }

    fun presentFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
            .addToBackStack(tag)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}