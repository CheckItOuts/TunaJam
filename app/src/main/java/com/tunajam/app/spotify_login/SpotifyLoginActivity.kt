package com.tunajam.app.spotify_login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.tunajam.app.R
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.tunajam.app.home.HomeActivity
import com.tunajam.app.user_data.UserData


const val CLIENT_ID = "385d1740c16f4437b66802d5d0886d44"
const val REDIRECT_URI = "com.tunajam.app://callback"
const val REQUEST_CODE = 1337 // On peut mettre n'importe quel nombre ici

class MainActivity : ComponentActivity() {
    private lateinit var btnSpot : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnSpot = findViewById(R.id.connectButton)
        btnSpot.setOnClickListener {
            authenticateWithSpotify()
        }
    }

    private fun navigateToHome(accessToken: String, refreshToken: String) {
        UserData.saveTokens(this, accessToken, refreshToken) // On sauvegarde les tokens
        // On navigue vers l'activité principale
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun authenticateWithSpotify() {
        val builder = AuthorizationRequest.Builder(
            CLIENT_ID,
            AuthorizationResponse.Type.CODE,
            REDIRECT_URI // L'URI de redirection après l'authentification
        )
        // Add offline access to get a refresh token
        builder.setShowDialog(true) // Afficher une boîte de dialogue pour l'authentification
        builder.setScopes(arrayOf("user-read-email", "user-library-read", "user-library-modify", "user-top-read",
            "playlist-modify-public","playlist-modify-private")) // Les permissions que l'on demande à l'utilisateur
        val request = builder.build() // On construit la requête d'authentification
        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request) // On ouvre l'activité d'authentification
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, data)
            when (response.type) {
                AuthorizationResponse.Type.CODE -> {
                    val authorizationCode = response.code
                    SpotifyAPI.exchangeCodeForTokens(authorizationCode) { accessToken, refreshToken, expiresIn ->
                        if (accessToken != null && refreshToken != null && expiresIn != null) {
                            navigateToHome(accessToken, refreshToken)
                        } else {
                            // Afficher un message d'erreur pour l'utilisateur
                            Toast.makeText(this,"Obtention des tokens impossible", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                AuthorizationResponse.Type.ERROR -> {
                    println(response.error)
                    // Afficher un message d'erreur pour l'utilisateur
                    Toast.makeText(this, "Erreur: ${response.error}", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // L'utilisateur a annulé l'authentification
                    Toast.makeText(this, "Connexion annulée", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

