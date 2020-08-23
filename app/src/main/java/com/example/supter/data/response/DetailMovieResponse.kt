package com.example.supter.data.response

import com.google.gson.annotations.SerializedName

data class DetailMovieResponse(
    @SerializedName("popularity") val popularity: Double,
    @SerializedName("id") val id: Double,
    @SerializedName("adult") val adult: Boolean,
    @SerializedName("poster_path") val poster_path : String?,
    @SerializedName("original_title") val original_title: String,
    @SerializedName("title") val title: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("release_date") val release_date: String,
    @SerializedName("vote_average") val vote_average : Double
)
