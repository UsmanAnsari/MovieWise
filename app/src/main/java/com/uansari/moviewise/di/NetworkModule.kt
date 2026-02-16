package com.uansari.moviewise.di

import com.uansari.moviewise.BuildConfig
import com.uansari.moviewise.data.remote.api.TmdbApiService
import com.uansari.moviewise.data.remote.interceptor.AuthInterceptor
import com.uansari.moviewise.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Kotlinx Serialization JSON configuration.
     */
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Logging interceptor — only active in DEBUG builds.
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    /**
     * OkHttpClient — the actual HTTP engine.
     *
     * Interceptors are added in ORDER. AuthInterceptor runs first
     * (adds the API key), then LoggingInterceptor runs (logs the
     * full request including the key — only in debug).
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor, loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder().addInterceptor(authInterceptor).addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS).build()

    /**
     * Retrofit instance — the HTTP client that reads TmdbApiService.
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient, json: Json
    ): Retrofit =
        Retrofit.Builder().baseUrl(Constants.BASE_URL).client(okHttpClient).addConverterFactory(
            json.asConverterFactory("application/json; charset=UTF8".toMediaType())
        ).build()

    @Provides
    @Singleton
    fun provideTmdbApiService(retrofit: Retrofit): TmdbApiService =
        retrofit.create(TmdbApiService::class.java)
}