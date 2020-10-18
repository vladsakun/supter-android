package com.supter.ui.moviedetail

import androidx.lifecycle.ViewModel
import com.supter.data.repository.PurchaseRepository
import com.supter.internal.lazyDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private  val purchaseRepository: PurchaseRepository
) : ViewModel() {

//    val movie by lazyDeferred {
//        purchaseRepository.getMovie()
//    }
//
//    fun fetchMovie(movieId: Double){
//        GlobalScope.launch {
//            purchaseRepository.fetchMovie(movieId)
//        }
//    }
}