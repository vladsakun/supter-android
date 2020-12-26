package com.supter.ui.main.purchase.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.response.ResultWrapper
import com.supter.data.response.UpdatePurchaseResponse
import com.supter.repository.PurchaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailPurchaseViewModel @ViewModelInject constructor(
    private val purchaseRepository: PurchaseRepository
) : ViewModel() {

    private val _updateResponseResultLiveData =
        MutableLiveData<ResultWrapper<UpdatePurchaseResponse>>()

    val updateResponseResultLiveData: LiveData<ResultWrapper<UpdatePurchaseResponse>> get() = _updateResponseResultLiveData

    fun deletePurchase(purchaseEntity: PurchaseEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            purchaseRepository.deletePurchase(purchaseEntity)
        }
    }

    fun updatePurchase(
        title: String,
        description: String,
        price: Double,
        purchaseEntity: PurchaseEntity
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            purchaseEntity.title = title
            purchaseEntity.description = description
            purchaseEntity.price = price
            _updateResponseResultLiveData.postValue(purchaseRepository.updateRemotePurchase(purchaseEntity))
        }
    }

}