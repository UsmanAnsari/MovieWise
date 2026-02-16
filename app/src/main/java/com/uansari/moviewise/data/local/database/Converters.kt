package com.uansari.moviewise.data.local.database

import androidx.room.TypeConverter
import com.uansari.moviewise.domain.util.MovieCategory

class Converters {
    @TypeConverter
    fun fromMovieCategory(category: MovieCategory) = category.name

    @TypeConverter
    fun toMovieCategory(category: String) = MovieCategory.valueOf(category)
}