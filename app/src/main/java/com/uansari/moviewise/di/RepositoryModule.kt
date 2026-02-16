package com.uansari.moviewise.di

import com.uansari.moviewise.data.repository.MovieRepositoryImpl
import com.uansari.moviewise.domain.repository.MovieRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Tells Hilt: whenever something asks for MovieRepository (the interface - Domain Layer),
 * provide MovieRepositoryImpl (the implementation - Data Layer).
 *
 * WHY @Binds INSTEAD OF @Provides:
 * @Provides creates a function body — you write new MovieRepositoryImpl(...)
 * @Binds is a pure declaration — no function body, just the mapping.
 * Hilt generates more efficient code with @Binds and it signals intent:
 * "this is purely an interface-to-implementation binding, nothing else."
 *
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(
        movieRepositoryImpl: MovieRepositoryImpl
    ): MovieRepository
}