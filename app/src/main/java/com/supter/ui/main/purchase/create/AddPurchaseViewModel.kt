package com.supter.ui.main.purchase.create

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supter.data.body.PurchaseBody
import com.supter.data.response.purchase.CreatePurchaseResponse
import com.supter.data.response.ResultWrapper
import com.supter.repository.PurchaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddPurchaseViewModel @ViewModelInject constructor(
    private val repository: PurchaseRepository
) : ViewModel() {

    private val _createPurchase = MutableLiveData<ResultWrapper<CreatePurchaseResponse>>()
    val createPurchaseResponse: LiveData<ResultWrapper<CreatePurchaseResponse>> get() = _createPurchase


    fun upsertPurchase(title: String, cost: Double, questionJson: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.createPurchase(PurchaseBody(title, cost))
            _createPurchase.postValue(response)
        }
    }
}