package com.supter.ui.main.dashboard

import androidx.lifecycle.ViewModel
import com.supter.data.repository.PurchaseRepository
import com.supter.internal.lazyDeferred

class DashboardViewModel(
    private val purchaseRepository: PurchaseRepository
) : ViewModel() {

    val purchaseList by lazyDeferred {
        purchaseRepository.getPurchaseList()
    }
}