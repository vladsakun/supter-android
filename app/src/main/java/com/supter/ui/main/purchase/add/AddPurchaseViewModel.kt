package com.supter.ui.main.purchase.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.repository.PurchaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddPurchaseViewModel(
    private val repository: PurchaseRepository
) : ViewModel() {

    fun upsertPurchase(purchaseEntity: PurchaseEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.upsertPurchase(purchaseEntity)
        }
    }
}