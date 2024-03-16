package com.tunajam.app.spotify_login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
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
import com.tunajam.app.home.HomeActivity
import com.tunajam.app.user_data.UserData
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


const val CLIENT_ID = "385d1740c16f4437b66802d5d0886d44"
const val CLIENT_SECRET = "fd0de51ee127491fb6472f89bcd149d5"
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
        builder.setScopes(arrayOf("user-read-email", "user-library-read", "user-library-modify")) // Les permissions que l'on demande à l'utilisateur
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
                    println(authorizationCode)
                    exchangeCodeForTokens(authorizationCode) { accessToken, refreshToken, expiresIn ->
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
fun exchangeCodeForTokens(authorizationCode: String, callback: (String?, String?, String?) -> Unit) {
    val formBodyBuilder = FormBody.Builder()
        .add("client_id", CLIENT_ID)
        .add("client_secret", CLIENT_SECRET)
        .add("redirect_uri", REDIRECT_URI)
        .add("code", authorizationCode)
        .add("grant_type", "authorization_code")


    val requestBody = formBodyBuilder.build()

    val request = Request.Builder()
        .url("https://accounts.spotify.com/api/token")
        .post(requestBody)
        .build()

    OkHttpClient().newCall(request).enqueue(object : Callback {
        override fun onResponse(call: Call, response: Response) {
            val responseBody = response.body?.string()
            val jsonObject = JSONObject(responseBody.toString())
            val accessToken = jsonObject.optString("access_token")
            val refreshToken = jsonObject.optString("refresh_token")
            val expiresIn = jsonObject.optInt("expires_in")
            callback(accessToken, refreshToken, expiresIn.toString())
        }

        override fun onFailure(call: Call, e: IOException) {
            // Handle failure
            callback(null, null, null)
        }
    })
}
