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
        movieRepository.getMovieList()
    }

    //Get searched movies list from api
    val searchedMovies by lazyDeferred {
        movieRepository.getSearchedMovies()
    }

    fun searchMovie(query: String) {
        GlobalScope.launch {
            movieRepository.searchMovies(query)
        }
    }
}