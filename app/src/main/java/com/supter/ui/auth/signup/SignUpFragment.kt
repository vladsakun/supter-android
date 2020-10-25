package com.supter.ui.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.supter.R
import com.supter.data.body.UserParams
import com.supter.data.network.Api
import com.supter.data.network.Status
import com.supter.data.response.Resp
import com.supter.databinding.SignupFragmentBinding
import com.supter.ui.ScopedFragment
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.kodein.di.android.x.di
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignUpFragment : ScopedFragment(), DIAware {

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
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, signUpViewModelFactory).get(SignUpViewModel::class.java)

        observeAuth()
        bindViews()
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
        viewModel.registerUser("test", "huko@email.com", "1234567")
    }

    private fun viewOneError(error: Error?) {
    }

    private fun viewOneSuccess(data: Resp?) {
        Toast.makeText(requireContext(), "Success", Toast.LENGTH_LONG).show()
    }

    private fun viewOneLoading() {

    }

    private fun bindViews() = launch {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}