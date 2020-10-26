package com.supter.ui.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.supter.data.network.Status
import com.supter.data.response.Resp
import com.supter.data.response.ResultWrapper
import com.supter.databinding.SignupFragmentBinding
import com.supter.ui.ScopedFragment
import org.kodein.di.DIAware
import org.kodein.di.android.x.di
import org.kodein.di.instance

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

        observeAuth()

        binding.signUpBtn.setOnClickListener {
            registerUser()
        }
    }

    private fun observeAuth() {
        viewModel.user.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> viewOneLoading()
                Status.SUCCESS -> viewOneSuccess(it.data)
                Status.ERROR -> viewOneError(it.error)
                Status.GENERIC_ERROR -> viewOneGenericError(it.message)
            }
        })
    }

    private fun registerUser() {
        viewModel.registerUser("test", "sakun11@email.com", "1234567")
    }

    private fun viewOneError(error: Error?) {
        Toast.makeText(requireContext(), error?.message, Toast.LENGTH_LONG).show()
    }

    private fun viewOneSuccess(data: Resp?) {
        Toast.makeText(requireContext(), "Success", Toast.LENGTH_LONG).show()
    }

    private fun viewOneLoading() {

    }

    private fun viewOneGenericError(message: String?) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}