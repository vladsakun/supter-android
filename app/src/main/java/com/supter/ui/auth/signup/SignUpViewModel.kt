package com.supter.ui.auth.signup

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.supter.repository.UserRepository
import com.supter.data.response.RegistrationResponse
import com.supter.data.response.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpViewModel @ViewModelInject constructor(
    val repository: UserRepository
) : ViewModel() {

    private val TAG = "SignUpViewModel"
    private val signUpResultMutableLiveData = MutableLiveData<ResultWrapper<RegistrationResponse>>()
    val signUp: LiveData<ResultWrapper<RegistrationResponse>> get() = signUpResultMutableLiveData

    fun registerUser(
        name: String,
        email: String,
        password: String,
    ) {

        viewModelScope.launch(Dispatchers.IO) {

            val response = repository.register(name, email, password)
            signUpResultMutableLiveData.postValue(response)

            when (response) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(response)
                is ResultWrapper.Success -> showSuccess(response.value)
            }
        }

    }

    private fun showSuccess(value: RegistrationResponse) {
        Log.d(TAG, "showSuccess: ")
    }

    private fun showGenericError(response: ResultWrapper.GenericError) {
        Log.d(TAG, "showGenericError: code: ${response.code} message ${response.error?.message}")
    }

    private fun showNetworkError() {
        Log.d(TAG, "showNetworkError: ")
    }

}