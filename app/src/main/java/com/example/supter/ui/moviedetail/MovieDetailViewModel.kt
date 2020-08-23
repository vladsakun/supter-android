package com.example.supter.ui.moviedetail

import androidx.lifecycle.ViewModel
import com.example.supter.data.repository.MovieRepository
import com.example.supter.internal.lazyDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private  val movieRepository: MovieRepository
) : ViewModel() {

    val movie by lazyDeferred {
        movieRepository.getMovie()
    }

    fun fetchMovie(movieId: Double){
        GlobalScope.launch {
            movieRepository.fetchMovie(movieId)
        }
    }
}