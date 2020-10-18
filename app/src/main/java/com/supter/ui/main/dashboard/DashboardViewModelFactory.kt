package com.supter.ui.main.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supter.data.repository.PurchaseRepository

class DashboardViewModelFactory(
    private val purchaseRepository: PurchaseRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DashboardViewModel(purchaseRepository) as T
    }
}