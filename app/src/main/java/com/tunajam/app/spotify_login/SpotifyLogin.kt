package com.tunajam.app.spotify_login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tunajam.app.R
import com.tunajam.app.ui.theme.TunaJamTheme
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

class MainActivity : ComponentActivity() {
    private lateinit var btnSpot : Button
    private val CLIENT_ID = "385d1740c16f4437b66802d5d0886d44"
    private val REDIRECT_URI = "com.tunajam.app://callback"
    private val REQUEST_CODE = 1337 // On peut mettre n'importe quel nombre ici


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnSpot = findViewById(R.id.connectButton)
        btnSpot.setOnClickListener {
            authenticateWithSpotify()
        }
    }
    private fun authenticateWithSpotify() {
        val builder = AuthorizationRequest.Builder(
            CLIENT_ID,
            AuthorizationResponse.Type.TOKEN,
            REDIRECT_URI // L'URI de redirection après l'authentification
        )
        builder.setScopes(arrayOf("user-read-email", "user-library-read")) // Les permissions que l'on demande à l'utilisateur

        val request = builder.build() // On construit la requête d'authentification
        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request) // On ouvre l'activité d'authentification
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, data)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    val accessToken = response.accessToken
                    println(accessToken)

                }
                AuthorizationResponse.Type.ERROR -> {
                }
                else -> {
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TunaJamTheme {
        Greeting("Android")
    }
}