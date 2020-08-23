package com.example.supter.ui.movielist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.supter.data.repository.MovieRepository

class MovieListViewModelFactory(
    private val movieRepository: MovieRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MovieListViewModel(movieRepository) as T
    }
}