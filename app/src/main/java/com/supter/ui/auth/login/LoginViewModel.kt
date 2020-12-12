package com.supter.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supter.repository.UserRepository
import com.supter.data.response.LoginResponse
import com.supter.data.response.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: UserRepository): ViewModel() {

    private val _loginResultMutableLiveData = MutableLiveData<ResultWrapper<LoginResponse>>()
    val loginResult:LiveData<ResultWrapper<LoginResponse>> get() = _loginResultMutableLiveData

    fun loginUser(
        username:String,
        password:String
    ){
        viewModelScope.launch(Dispatchers.IO){
            val response = authRepository.login(username, password)
            _loginResultMutableLiveData.postValue(response)
        }
    }


}