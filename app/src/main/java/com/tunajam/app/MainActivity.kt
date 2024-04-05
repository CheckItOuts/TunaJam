package com.tunajam.app

import android.content.Context
import android.graphics.Typeface
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
        //set up custom fonts
        getTypefaceFromFont(this@MainActivity, "font/art_nuvo.ttf")
        setContent {
            TunaJamTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    TunaJamApp(context = this@MainActivity)
                }
            }
        }
    }
}
fun getTypefaceFromFont(context: Context, fontPath: String): Typeface? {
    return try {
        Typeface.createFromAsset(context.assets, fontPath)
    } catch (e: Exception) {
        e.printStackTrace()
        System.out.println("FONT NOT FOUND")
        null
    }
}