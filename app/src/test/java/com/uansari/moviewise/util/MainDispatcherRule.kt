package com.uansari.moviewise.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * JUnit rule that sets the Main dispatcher to a TestDispatcher.
 *
 * WHY THIS EXISTS:
 * ViewModels use viewModelScope which uses Dispatchers.Main.
 * Main dispatcher doesn't exist in unit tests (it's Android UI thread).
 * This rule replaces it with a test dispatcher for unit tests.
 *
 * USAGE:
 * @get:Rule
 * val mainDispatcherRule = MainDispatcherRule()
 */
@ExperimentalCoroutinesApi
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}