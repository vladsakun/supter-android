package com.supter.ui.auth.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.supter.R
import com.supter.data.response.Resp
import com.supter.data.response.ResultWrapper
import com.supter.databinding.SignupFragmentBinding
import com.supter.ui.ScopedFragment
import com.supter.ui.main.MainActivity
import com.supter.utils.SystemUtils
import es.dmoral.toasty.Toasty
import org.kodein.di.DIAware
import org.kodein.di.android.x.di
import org.kodein.di.instance
import java.lang.Exception

class SignUpFragment : ScopedFragment(), DIAware {

    private val TAG = "SignUpFragment"

    override val di by di()
    private val signUpViewModelFactory: SignUpViewModelFactory by instance()

    private lateinit var viewModel: SignUpViewModel

    private var _binding: SignupFragmentBinding? = null
    private val binding: SignupFragmentBinding get() = _binding!!

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

        viewModel = ViewModelProvider(this, signUpViewModelFactory).get(SignUpViewModel::class.java)

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
        Log.d(TAG, "showError: ")
        try {
            Toasty.error(requireContext(), errorMessage, Toast.LENGTH_SHORT, true).show()
        } catch (e: Exception) {
            Log.e(TAG, "showError: ", e)
        }
    }

    private fun registerUser() {
        viewModel.registerUser("alexander", "vladsakun4@gmail.com", "123456789")
    }

    private fun successSignUp(result: ResultWrapper.Success<Resp>) {
        SystemUtils.saveToken(requireContext().applicationContext, result.value.data.access_token)
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