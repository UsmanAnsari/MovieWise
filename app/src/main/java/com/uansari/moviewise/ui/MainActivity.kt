package com.uansari.moviewise.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.uansari.moviewise.ui.navigation.MovieNavGraph
import com.uansari.moviewise.ui.theme.MovieWiseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieWiseTheme {
                MovieNavGraph()
            }
        }
    }
}