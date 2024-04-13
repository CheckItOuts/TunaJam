package com.tunajam.app.spotify_login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.tunajam.app.R
import com.tunajam.app.firebase.Database
import com.tunajam.app.home.HomeActivity
import com.tunajam.app.spotify_login.MainActivity.Companion.authenticateWithSpotify
import com.tunajam.app.ui.theme.TunaJamBleuPale
import com.tunajam.app.ui.theme.TunaJamViolet
import com.tunajam.app.ui.theme.Typography
import com.tunajam.app.user_data.UserData


const val CLIENT_ID = "385d1740c16f4437b66802d5d0886d44"
const val REDIRECT_URI = "com.tunajam.app://callback"
const val REQUEST_CODE = 1337 // On peut mettre n'importe quel nombre ici

/**
 * L'activité de connexion à Spotify qui est automatiquement appelée au lancement de l'application.
 */
class MainActivity : ComponentActivity() {

    /**
     * On crée l'interface utilisateur de l'activité.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val accessToken = UserData.getAccessToken(this)
        val refreshToken = UserData.getRefreshToken(this)
        if (((accessToken != null) or (accessToken == "accessToken")) && ((refreshToken != null) or (refreshToken == "refreshToken"))) {
            val spotifyAPI = SpotifyAPI()
            if (refreshToken != null && ((accessToken == "accessToken") or (accessToken == null))) {
                println(refreshToken)
                spotifyAPI.refreshAccessToken(this,refreshToken) { newAccessToken ->
                    if (newAccessToken != null) {
                        UserData.saveTokens(this, newAccessToken, refreshToken)
                    }
                }
            }
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        setContent {
            LoginScreen(this@MainActivity)
        }
    }
    /**
     * Cette fonction est appelée lorsque l'utilisateur a été authentifié avec Spotify.
     * Elle permet de naviguer vers l'activité principale de l'application.
     */
    private fun navigateToHome(accessToken: String, refreshToken: String) {
        val db = Database()
        val spotifyAPI = SpotifyAPI()
        spotifyAPI.getUserProfile(accessToken, this@MainActivity){displayName,emailUser,_,imgUsrUrl ->
            UserData.saveUserName(this, displayName.toString())
            UserData.saveUserEmail(this,emailUser.toString())
            var imgUsr = imgUsrUrl
            if (imgUsrUrl == null) {
                imgUsr = "https://pbs.twimg.com/profile_images/1611108206050250759/ORaNxrfb_400x400.jpg"
            }
            UserData.saveUserImgUrl(this,imgUsr.toString())
            db.addUser(displayName.toString(), imgUsr.toString()) // On ajoute l'utilisateur à la base de données
        }
        UserData.saveTokens(this, accessToken, refreshToken) // On sauvegarde les tokens
        spotifyAPI.getUserTopTracks(this, accessToken, refreshToken) { tracks ->
            if (tracks != null) {
                for (i in 0 until tracks.size) {
                    db.addMusic(UserData.getUserName(this).toString(), tracks[i])
                }
            }
        }
        // On navigue vers l'activité principale
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Cette fonction est appelée lorsque l'utilisateur a été authentifié avec Spotify.
     * Elle permet de récupérer le code d'authentification et d'échanger ce code contre des tokens d'accès.
     */
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

    /**
     * Cette fonction permet de lancer l'authentification avec Spotify.
     */
    companion object {
        fun authenticateWithSpotify(mainActivity: MainActivity) {
            val builder = AuthorizationRequest.Builder(
                CLIENT_ID,
                AuthorizationResponse.Type.CODE,
                REDIRECT_URI // L'URI de redirection après l'authentification
            )
            // Add offline access to get a refresh token
            builder.setShowDialog(true) // Afficher une boîte de dialogue pour l'authentification
            builder.setScopes(arrayOf("user-read-email", "user-library-read", "user-library-modify", "user-top-read",
                "playlist-modify-public","playlist-modify-private","playlist-read-private"
                ,"playlist-read-collaborative")) // Les permissions que l'on demande à l'utilisateur
            val request = builder.build() // On construit la requête d'authentification
            AuthorizationClient.openLoginActivity(mainActivity, REQUEST_CODE, request) // On ouvre l'activité d'authentification
        }
    }
}


@Composable
fun LoginScreen(mainActivity: MainActivity) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ElevatedButton(onClick = { authenticateWithSpotify(mainActivity)},
                modifier = Modifier.padding(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 12.dp),
                colors = ButtonColors(
                    containerColor = TunaJamViolet,
                    contentColor = Color.White,
                    disabledContainerColor = TunaJamBleuPale,
                    disabledContentColor = Color.White
                ),
                contentPadding = PaddingValues(30.dp)) {
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                )
                {
                    Image(
                        painter = painterResource(id = R.drawable.ic_spotify_login),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .width(200.dp)
                            .height(200.dp)
                    )
                    Text(text = "Connexion avec Spotify",
                        textAlign = TextAlign.Center,
                        style = Typography.titleLarge)
                }

            }
        }
    }
}
