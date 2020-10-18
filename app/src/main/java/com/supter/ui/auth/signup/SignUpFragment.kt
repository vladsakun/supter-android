package com.supter.ui.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.supter.R
import com.supter.data.body.UserParams
import com.supter.data.network.PurchaseApiService
import com.supter.data.response.Resp
import com.supter.databinding.SignupFragmentBinding
import com.supter.ui.ScopedFragment
import kotlinx.coroutines.launch
import okhttp3.Callback
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

    lateinit var mBinding: SignupFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.signup_fragment, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, signUpViewModelFactory).get(SignUpViewModel::class.java)

        val okHttpClient = OkHttpClient.Builder()
            .build()

        //Build Retrofit
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(PurchaseApiService.BASE_URL)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PurchaseApiService::class.java)

        val call = retrofit.register(UserParams("test", "pasha123@gmail.com", "1234567"))

        call.enqueue(object : retrofit2.Callback<Resp> {
            override fun onResponse(call: Call<Resp>, response: Response<Resp>) {
                Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()

            }

            override fun onFailure(call: Call<Resp>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
            }

        })

        bindViews()
    }

    private fun bindViews() = launch {

        val authSuccessfully = viewModel.user.await()

        authSuccessfully.observe(viewLifecycleOwner, object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
                if (t != null) {
                    Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
                }
            }
        })

        mBinding.signInBtn.setOnClickListener {
//            viewModel.registerUser(
//                "test",
//                "stilk43@gmail.com",
//                "1234567"
//            )
        }
    }

}