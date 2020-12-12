package com.supter.ui.main.purchase.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supter.data.db.entity.PurchaseEntity
import com.supter.repository.PurchaseRepository
import com.supter.utils.enums.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class AddPurchaseViewModel(
    private val repository: PurchaseRepository
) : ViewModel() {

    fun upsertPurchase(purchaseEntity: PurchaseEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.upsertPurchase(purchaseEntity)
        }
    }

    fun upsertPurchase(name:String, cost:Double, questionJson:String?){
        upsertPurchase(PurchaseEntity(
            name,
            cost,
            Priority.LOW.ordinal,
            "wish",
            questionJson,
            0.0,
            0.0,
            null
        ))
    }
}