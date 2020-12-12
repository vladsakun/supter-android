package com.supter.ui.main.purchase.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supter.repository.PurchaseRepository

class AddPurchaseViewModelFactory(
    private val repository: PurchaseRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddPurchaseViewModel(repository) as T
    }
}