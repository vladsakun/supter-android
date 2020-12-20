package com.supter.ui.auth.signup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.supter.R
import com.supter.data.response.RegistrationResponse
import com.supter.data.response.ResultWrapper
import com.supter.databinding.SignupFragmentBinding
import com.supter.ui.main.MainActivity
import com.supter.utils.SystemUtils
import com.supter.utils.logException
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: SignupFragmentBinding? = null
    private val binding: SignupFragmentBinding get() = _binding!!

    private val TAG = "SignUpFragment"

    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = SignupFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews()
    }

    private fun bindViews() {
        viewModel.signUp.observe(viewLifecycleOwner, { result ->
            when (result) {

                is ResultWrapper.NetworkError -> {
                    showError(requireContext().getString(R.string.no_internet_connection))
                }

                is ResultWrapper.Success -> {
                    successSignUp(result)
                }

                is ResultWrapper.GenericError -> {
                    showError(
                        result.error?.message
                            ?: requireContext().getString(R.string.no_internet_connection)
                    )
                }
            }
        })

        binding.signUpBtn.setOnClickListener {
            registerUser()
        }

    }

    private fun showError(errorMessage: String) {
        try {
            Toasty.error(requireContext(), errorMessage, Toast.LENGTH_SHORT, true).show()
        } catch (e: Exception) {
            logException(e)
        }
    }

    private fun registerUser() {
        viewModel.registerUser(
            binding.name.text.toString(),
            binding.username.text.toString(),
            binding.password.text.toString()
        )
    }

    private fun successSignUp(result: ResultWrapper.Success<RegistrationResponse>) {
        SystemUtils.saveToken(requireContext().applicationContext, result.value.accessToken)
        startMainActivity()
    }

    private fun startMainActivity() {
        requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}