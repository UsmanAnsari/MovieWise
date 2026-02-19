package com.uansari.moviewise

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.uansari.moviewise.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenSmokeTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun app_launches_and_shows_title() {
        // Wait for content to load (give it a few seconds)
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("MovieWise")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Verify the title is displayed
        composeTestRule
            .onNodeWithText("MovieWise")
            .assertIsDisplayed()
    }
}