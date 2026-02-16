package com.uansari.moviewise.data.util

import com.uansari.moviewise.domain.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * A reusable utility that implements the offline-first
 * NetworkBoundResource pattern as a Kotlin Flow.
 *
 * TYPE PARAMETERS:
 * ResultType   → the type your app works with (e.g. List<Movie>)
 *                This is what Room stores and what the UI consumes.
 * RequestType  → the type the API returns (e.g. MovieListResponseDto)
 *                Separated from ResultType because the API response
 *                needs mapping before it can be saved to Room.
 *
 * PARAMETERS:
 * query          → how to read from Room (returns a live Flow)
 * fetch          → how to call the API (suspend function)
 * saveFetchResult → how to save the API response to Room
 * shouldFetch    → whether to hit the network at all (default: always)
 * onFetchFailed  → callback if the API call throws (for logging etc.)
 *
 * EMISSION SEQUENCE:
 *
 * 1. Read Room → emit Resource.Loading(cachedData)
 *    The UI gets data immediately — even if it's an empty list.
 *    The optional 'data' in Loading is what enables showing
 *    cached content while a background refresh is in flight.
 *
 * 2. Fetch from API → save to Room
 *    Room is the single source of truth.
 *    We never pass API data directly to the UI.
 *
 * 3a. Fetch succeeds → emitAll(Room flow as Success)
 *     Room emits the saved data. The Flow stays alive — any future
 *     Room changes (e.g. from another screen) re-emit automatically.
 *
 * 3b. Fetch fails → emit Resource.Error(message, cachedData)
 *     The user still sees their cached data.
 *     Flow ends — restarted on retry/refresh.
 */
fun <ResultType, RequestType> networkBoundResource(
    query: () -> Flow<ResultType>,
    fetch: suspend () -> RequestType,
    saveFetchResult: suspend (RequestType) -> Unit,
    shouldFetch: (ResultType) -> Boolean = { true },
    onFetchFailed: (Throwable) -> Unit = { }
): Flow<Resource<ResultType>> = flow {

    // Step 1: Read from local cache
    val cachedData = query().first()

    if (shouldFetch(cachedData)) {

        // Step 2: Emit cached data with Loading state

        // The UI renders this immediately — no blank screen, no spinner over
        // empty space. If cache is empty, Loading(data = emptyList) signals
        // "first load" to the ViewModel. If cache has data, Loading(data = movies)
        // signals "background refresh in progress".
        emit(Resource.Loading(data = cachedData))

        try {
            // Step 3a: Fetch, save, collect ────────────────────────────────
            saveFetchResult(fetch())

            // After saveFetchResult, Room has fresh data.
            // emitAll keeps this Flow alive — Room's Flow never completes,
            // so any future cache updates automatically flow to the UI.
            emitAll(
                query().map { freshData -> Resource.Success(freshData) })

        } catch (throwable: Throwable) {

            // Step 3b: Fetch failed
            onFetchFailed(throwable)

            // Emit Error but include cached data so the UI isn't wiped.
            // Resource.Error.data lets the ViewModel decide:
            // "do I have data to keep showing, or do I show the error screen?"
            emit(
                Resource.Error(
                    message = throwable.message ?: "An unknown error occurred", data = cachedData
                )
            )
        }

    } else {
        // shouldFetch returned false — serve from cache only
        emitAll(
            query().map { Resource.Success(it) })
    }
}