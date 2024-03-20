package com.tunajam.app.home

import android.annotation.SuppressLint
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tunajam.app.spotify_login.SpotifyAPI
import com.tunajam.app.ui.theme.TunaJamTheme
import com.tunajam.app.user_data.UserData


data class User(val name: String)

class HomeActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val accessToken = UserData.getAccessToken(this).toString()
        val refreshToken = UserData.getRefreshToken(this).toString()
        val spotifyAPI = SpotifyAPI()
        SpotifyAPI.getUserRecommendation(this,accessToken,refreshToken) { tracks ->
            spotifyAPI.getUserProfile(accessToken) { displayName, _, _, _ ->
                val user = User(displayName.toString())
                runOnUiThread {
                    if (tracks != null) {
                        for (i in 0 until tracks.size) {
                            val json = tracks[i]
                            // Afficher le nom de la chanson dans la console pour le moment
                            println(json.get("name"))
                        }
                    }
                    setContent {
                        TunaJamTheme {
                            UserInfo(user = user)
                        }
                    }
                }
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserInfo(user:User){
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Accueil") }
            )
        },
        content = {
            Surface(
                modifier = Modifier.fillMaxSize(),
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Nom: ${user.name}")
                    Spacer(modifier = Modifier.height(16.dp))
                    // Display other user information as needed
                }
            }
        }
    )
}


    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        TunaJamTheme {
            UserInfo(user = User("John Doe"))
        }
    }
}