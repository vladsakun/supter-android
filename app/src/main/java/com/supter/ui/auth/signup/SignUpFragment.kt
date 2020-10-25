package com.supter.ui.auth.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.supter.data.network.Status
import com.supter.data.response.Resp
import com.supter.databinding.SignupFragmentBinding
import com.supter.ui.ScopedFragment
import kotlinx.coroutines.launch
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.kodein.di.android.x.di

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

        binding.signUpBtn.setOnClickListener{
            registerUser()
        }
    }

    private fun observeAuth() {
        viewModel.user.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> viewOneLoading()
                Status.SUCCESS -> viewOneSuccess(it.data)
                Status.ERROR -> viewOneError(it.error)
            }
        })
    }

    private fun registerUser(){
        viewModel.registerUser("test", "sakun8@email.com", "1234567")
    }

    private fun viewOneError(error: Error?) {
        Toast.makeText(requireContext(), error?.message, Toast.LENGTH_LONG).show()
    }

    private fun viewOneSuccess(data: Resp?) {
        Log.d(TAG, "viewOneSuccess: ")
    }

    private fun viewOneLoading() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}