package com.supter.ui.movielist

import androidx.lifecycle.ViewModel
import com.supter.repository.PurchaseRepository
import com.supter.internal.lazyDeferred

class MovieListViewModel(
    private val movieRepository: PurchaseRepository
) : ViewModel() {

    //Get movie list from db
    val movieList by lazyDeferred {
        movieRepository.getPurchaseList()
    }
}