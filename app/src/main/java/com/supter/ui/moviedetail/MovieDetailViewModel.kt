package com.supter.ui.moviedetail

import androidx.lifecycle.ViewModel
import com.supter.repository.PurchaseRepository

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