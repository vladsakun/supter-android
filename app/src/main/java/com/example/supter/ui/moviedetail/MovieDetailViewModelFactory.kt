package com.example.supter.ui.moviedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.supter.data.repository.MovieRepository

class MovieDetailViewModelFactory(
    private val movieRepository: MovieRepository
) :ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MovieDetailViewModel(movieRepository) as T
    }
}