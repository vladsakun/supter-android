package com.supter.ui.auth.signup

import androidx.lifecycle.MutableLiveData
import com.supter.data.body.UserParams
import com.supter.data.network.Event
import com.supter.data.repository.AuthRepository
import com.supter.data.response.Resp
import com.supter.ui.BaseViewModel

class SignUpViewModel : BaseViewModel() {

    val user = MutableLiveData<Event<Resp>>()

    fun registerUser(
        name: String,
        email: String,
        password: String,
    ) {
        requestWithLiveData(user) {
            api.registerUser(UserParams(name, email, password))
        }
    }
}