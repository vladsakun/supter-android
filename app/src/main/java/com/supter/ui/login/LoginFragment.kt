package com.supter.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.supter.R
import com.supter.databinding.LoginFragmentBinding

class LoginFragment : Fragment() {

    lateinit var mBinding: LoginFragmentBinding
    lateinit var mActivity: LoginActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false)

        mActivity = requireActivity() as LoginActivity
        mBinding.signUpBtn.setOnClickListener(signUpClickListener)

        return mBinding.root
    }

    var signUpClickListener: View.OnClickListener = View.OnClickListener {
        mActivity.presentFragment(SignupFragment(), "SignupFragment")
    }
}