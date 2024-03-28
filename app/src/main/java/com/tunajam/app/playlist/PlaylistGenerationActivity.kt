package com.tunajam.app.playlist

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tunajam.app.home.HomeActivity
import com.tunajam.app.spotify_login.SpotifyAPI
import com.tunajam.app.user_data.PlaylistData
import com.tunajam.app.user_data.UserData
import org.json.JSONArray
import org.json.JSONObject


class PlaylistGenerationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlaylistGenerationPage(onClickHome = {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish() }, context = this)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistGenerationPage(onClickHome : () -> Unit, context: Context){
    val selectedFriends by remember { mutableStateOf(emptyList<String>()) }
    var maxAcousticness by remember { mutableFloatStateOf(0.5f) }
    var maxDanceability by remember { mutableFloatStateOf(0.5f) }
    var maxInstrulmentalness by remember { mutableFloatStateOf(0.5f) }
    var maxValence by remember { mutableFloatStateOf(0.5f) }
    var maxSpeechiness by remember { mutableFloatStateOf(0.5f) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text="Génération de playlist",
                    style = MaterialTheme.typography.headlineSmall)}

            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 65.dp)
            ) {
                // Friend selection
                Text(
                    text = "Sélectionne tes amis :",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 15.dp)
                )
                // TODO : ajouter la liste des amis
                // Parameter sliders
                Text(
                    text = "Ajuste tes paramètres:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 15.dp).align(CenterHorizontally)
                )
                ParameterSlider("Acoustique", maxAcousticness) {maxAcousticness = it }
                ParameterSlider("Dansant", maxDanceability) { maxDanceability = it }
                ParameterSlider("Instrumental", maxInstrulmentalness ) {maxInstrulmentalness = it }
                ParameterSlider("Positivité", maxValence) { maxValence = it }
                ParameterSlider("Paroles", maxSpeechiness ) {maxSpeechiness  = it }

                Spacer(modifier = Modifier.height(16.dp))

                // Generate playlist button
                Button(onClick = {
                    generatePlaylist(context,selectedFriends, maxAcousticness, maxDanceability,
                        maxInstrulmentalness, maxValence, maxSpeechiness)
                },
                    modifier = Modifier.align(CenterHorizontally).padding(top = 16.dp)
                ) {
                    Text("Générer la playlist")
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {
                    onClickHome()
                },
                    modifier = Modifier.align(CenterHorizontally)
                ) {
                    Text("Retour à l'accueil")
                }
            }
        }
    )
}

@Composable
fun ParameterSlider(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(label)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1f,
            steps = 100,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

val friendsList = listOf("Friend 1", "Friend 2", "Friend 3", "Friend 4") // TODO : remplacer par la liste des amis de l'utilisateur

/**
 * Génère une playlist en fonction des paramètres donnés.
 */
fun generatePlaylist(context : Context, friends: List<String>, maxAcousticness: Float,
                     maxDanceability: Float, maxInstrulmentalness: Float,
                     maxValence: Float, maxSpeechiness: Float) {
    val accessToken = UserData.getAccessToken(context).toString()
    val refreshToken = UserData.getRefreshToken(context).toString()
    val parameters = mutableMapOf(
        "max_acousticness" to mutableListOf(maxAcousticness.toString()),
        "max_danceability" to mutableListOf(maxDanceability.toString()),
        "max_instrumentalness" to mutableListOf(maxInstrulmentalness.toString()),
        "max_valence" to mutableListOf(maxValence.toString()),
        "max_speechiness" to mutableListOf(maxSpeechiness.toString()),
        // Il faut gérer la liste des genres musicaux
        "seed_genres" to mutableListOf("pop")
    )
    SpotifyAPI.getGeneratedPlaylistTracks(context, accessToken, refreshToken, parameters) { tracks ->
        println(tracks)
        if (tracks != null) {
            PlaylistData.savePlaylist(context,tracks)
            val intent = Intent(context, PlaylistDisplayActivity::class.java)
            context.startActivity(intent)
        }
    }
}
