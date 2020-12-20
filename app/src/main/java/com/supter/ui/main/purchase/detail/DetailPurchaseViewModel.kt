package com.supter.ui.main.purchase.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supter.data.db.entity.PurchaseEntity
import com.supter.repository.PurchaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailPurchaseViewModel @ViewModelInject constructor(
        private val purchaseRepository: PurchaseRepository
) : ViewModel()  {

    fun deletePurchase(purchaseEntity: PurchaseEntity){
        viewModelScope.launch(Dispatchers.IO){
            purchaseRepository.deletePurchase(purchaseEntity)
        }
    }

}