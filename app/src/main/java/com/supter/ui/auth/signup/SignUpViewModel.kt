package com.supter.ui.auth.signup

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.supter.data.repository.AuthRepository
import com.supter.data.response.Resp
import com.supter.data.response.ResultWrapper
import com.supter.ui.BaseViewModel
import kotlinx.coroutines.launch

class SignUpViewModel(val repository: AuthRepository) : BaseViewModel() {

    private val TAG = "SignUpViewModel"
    val user = repository.registrationEventLiveData

    fun registerUser(
        name: String,
        email: String,
        password: String,
    ) {

//        repository.registerUser(name, email, password)

        viewModelScope.launch {
            val response = repository.getRegisteredUser(name, email, password)
            when (response) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(response)
                is ResultWrapper.Success -> showSuccess(response.value)
            }
        }

//        requestWithLiveData(user) {
//            api.registerUser(UserParams(name, email, password))
//        }
    }

    private fun showSuccess(value: Resp) {
        Log.d(TAG, "showSuccess: ")
    }

    private fun showGenericError(response: ResultWrapper.GenericError) {
        Log.d(TAG, "showGenericError: " + response.error?.message)
    }

    private fun showNetworkError() {
        Log.d(TAG, "showNetworkError: ")
    }

}