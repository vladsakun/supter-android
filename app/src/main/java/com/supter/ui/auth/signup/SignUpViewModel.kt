package com.supter.ui.auth.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supter.data.repository.AuthRepository
import com.supter.data.response.Resp
import com.supter.data.response.ResultWrapper
import kotlinx.coroutines.launch

class SignUpViewModel(val repository: AuthRepository) : ViewModel() {

    private val TAG = "SignUpViewModel"
    private val signUpResultMutableLiveData = MutableLiveData<ResultWrapper<Resp>>()
    val signUp: LiveData<ResultWrapper<Resp>> get() = signUpResultMutableLiveData

    fun registerUser(
        name: String,
        email: String,
        password: String,
    ) {

        viewModelScope.launch {

            val response = repository.register(name, email, password)
            signUpResultMutableLiveData.postValue(response)

            when (response) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(response)
                is ResultWrapper.Success -> showSuccess(response.value)
            }
        }

    }

    private fun showSuccess(value: Resp) {
        Log.d(TAG, "showSuccess: ")
    }

    private fun showGenericError(response: ResultWrapper.GenericError) {
        Log.d(TAG, "showGenericError: code: ${response.code} message ${response.error?.message}")
    }

    private fun showNetworkError() {
        Log.d(TAG, "showNetworkError: ")
    }

}