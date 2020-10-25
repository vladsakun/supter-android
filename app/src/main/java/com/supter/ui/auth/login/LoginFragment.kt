package com.supter.ui.auth.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.supter.R
import com.supter.databinding.LoginFragmentBinding
import com.supter.ui.auth.LoginActivity
import com.supter.ui.auth.signup.SignUpFragment

class LoginFragment : Fragment() {

    private var _binding: LoginFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var mActivity: LoginActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = LoginFragmentBinding.inflate(inflater, container, false)

        val view = binding.root
        mActivity = requireActivity() as LoginActivity


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signUpBtn.setOnClickListener(signUpClickListener)
    }

    var signUpClickListener: View.OnClickListener = View.OnClickListener {
        mActivity.presentFragment(SignUpFragment(), "SignupFragment")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}