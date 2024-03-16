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
import androidx.compose.ui.unit.dp
import com.tunajam.app.spotify_login.SpotifyAPI
import com.tunajam.app.ui.theme.TunaJamTheme
import com.tunajam.app.user_data.UserData


data class User(val name: String)

class HomeActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val accessToken = UserData.getAccessToken(this)
        val spotifyAPI = SpotifyAPI()
        if (accessToken != null) {
            spotifyAPI.getUserProfile(accessToken) { displayName ->
                val user = User(displayName ?: "Unknown")
                runOnUiThread {
                    setContent {
                        TunaJamTheme {
                            UserInfo(user = user)
                        }
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
                title = { Text(text = "User Profile") }
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
                    Text(text = "Name: ${user.name}")
                    Spacer(modifier = Modifier.height(16.dp))
                    // Display other user information as needed
                }
            }
        }
    )
}
