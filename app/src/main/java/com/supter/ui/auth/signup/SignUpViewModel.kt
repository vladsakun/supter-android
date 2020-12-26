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

        }

    }
}