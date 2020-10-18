package com.supter.ui.movielist

import androidx.lifecycle.ViewModel
import com.supter.data.repository.PurchaseRepository
import com.supter.internal.lazyDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MovieListViewModel(
    private val movieRepository: PurchaseRepository
) : ViewModel() {

    //Get movie list from db
    val movieList by lazyDeferred {
        movieRepository.getPurchaseList()
    }
}