package com.example.freshtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.freshtrack.ui.navigation.AppNavGraph
import com.example.freshtrack.ui.theme.FreshTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FreshTrackTheme {
                AppNavGraph()
            }
        }
    }
}
