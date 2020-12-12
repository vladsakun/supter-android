package com.supter.ui.movielist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supter.repository.PurchaseRepository

class MovieListViewModelFactory(
    private val movieRepository: PurchaseRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MovieListViewModel(movieRepository) as T
    }
}