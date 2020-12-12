package com.supter.ui.main.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supter.data.db.entity.PurchaseEntity
import com.supter.repository.PurchaseRepository
import com.supter.internal.lazyDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val purchaseRepository: PurchaseRepository
) : ViewModel() {

    fun updatePurchase(dragItem: PurchaseEntity, toColumn: Int) {
        when (toColumn) {
            0 -> dragItem.status = "wish"
            1 -> dragItem.status = "process"
            2 -> dragItem.status = "done"
        }

        viewModelScope.launch(Dispatchers.IO) {
            purchaseRepository.upsertPurchase(dragItem)
        }
    }

    val purchaseList by lazyDeferred {
        purchaseRepository.getPurchaseList()
    }
}