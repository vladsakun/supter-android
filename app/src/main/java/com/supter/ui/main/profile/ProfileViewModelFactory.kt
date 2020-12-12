package com.supter.ui.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supter.repository.UserRepository

class ProfileViewModelFactory(
    private val authRepository:
    UserRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileViewModel(authRepository) as T
    }
}