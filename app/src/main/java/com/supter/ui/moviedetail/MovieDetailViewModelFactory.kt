package com.supter.ui.moviedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supter.data.repository.PurchaseRepository

class MovieDetailViewModelFactory(
    private val purchaseRepository: PurchaseRepository
) :ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MovieDetailViewModel(purchaseRepository) as T
    }
}