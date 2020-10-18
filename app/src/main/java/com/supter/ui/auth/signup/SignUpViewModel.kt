package com.supter.ui.auth.signup

import androidx.lifecycle.ViewModel
import com.supter.data.repository.AuthRepository
import com.supter.internal.lazyDeferred

class SignUpViewModel(val repository: AuthRepository) : ViewModel() {

    val user by lazyDeferred { repository.isAuthSuccessful }

    fun registerUser(
        name: String,
        email: String,
        password: String,
    ) {
        repository.registerUser(name, email, password)
    }
}