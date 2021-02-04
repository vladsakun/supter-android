package com.supter.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.supter.R
import com.supter.data.response.ResultWrapper
import com.supter.databinding.LoginFragmentBinding
import com.supter.ui.auth.LoginActivity
import com.supter.ui.auth.signup.SignUpFragment
import com.supter.ui.main.MainActivity
import com.supter.utils.logException
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: LoginFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
    }

    private fun bindViews() {
        binding.signInBtn.setOnClickListener(signInClickListener)
        binding.signUpBtn.setOnClickListener(signUpClickListener)

        initObservers()
    }

    private fun initObservers() {
        viewModel.loginResult.observe(viewLifecycleOwner , Observer{ result ->

            when (result) {
                is ResultWrapper.Success -> startMainActivity()

                is ResultWrapper.NetworkError -> {
                    showError(requireContext().getString(R.string.no_internet_connection))
                }

                is ResultWrapper.GenericError -> {
                    showError(
                        result.error?.message
                            ?: requireContext().getString(R.string.no_internet_connection)
                    )
                }
            }
        })
    }

    private fun startMainActivity() {
        requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
        requireActivity().finish()
    }

    private var signUpClickListener: View.OnClickListener = View.OnClickListener {
        (requireActivity() as LoginActivity).presentFragment(
            SignUpFragment(),
            SignUpFragment::class.java.simpleName
        )
    }

    private var signInClickListener: View.OnClickListener = View.OnClickListener {
        viewModel.loginUser(
            binding.email.text.toString().trim(),
            binding.password.text.toString().trim()
        )
    }

    private fun showError(errorMessage: String) {
        try {
            Toasty.error(requireContext(), errorMessage, Toast.LENGTH_SHORT, true).show()
        } catch (e: Exception) {
            logException(e)
        }
    }
}