package com.supter.ui.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supter.data.db.entity.UserEntity
import com.supter.data.response.AccountResponse
import com.supter.data.response.ResultWrapper
import com.supter.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: UserRepository
) : ViewModel() {

    private val _accountResponse = MutableLiveData<ResultWrapper<AccountResponse>>()
    val accountResponse: LiveData<ResultWrapper<AccountResponse>> get() = _accountResponse

    private val _account = MutableLiveData<UserEntity?>()
    val account: LiveData<UserEntity?> get() = _account

    fun upsertUser(
        name: String,
        incomeRemainder: Double,
        amount: Double,
        period: Double
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _accountResponse.postValue(
                authRepository.putUser(
                    name,
                    incomeRemainder,
                    amount,
                    period
                )
            )
        }
    }

    fun getUser(): LiveData<UserEntity?> {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.getUser().collect {
                _account.postValue(it)
            }
        }
        return account
    }
}