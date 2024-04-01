package com.tunajam.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.tunajam.app.ui.theme.TunaJamTheme
import com.tunajam.app.ui.TunaJamApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            TunaJamTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    TunaJamApp()
                }
            }
        }
    }
}