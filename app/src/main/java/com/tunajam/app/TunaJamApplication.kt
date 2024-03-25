package com.tunajam.app
import androidx.activity.ComponentActivity
import com.tunajam.app.data.AppContainer
import android.app.Application
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.tunajam.app.data.DefaultAppContainer

class TunaJamApplication : ComponentActivity() {
    /** AppContainer instance used by the rest of classes to obtain dependencies */
    lateinit var container: AppContainer
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        container = DefaultAppContainer()
    }
}
