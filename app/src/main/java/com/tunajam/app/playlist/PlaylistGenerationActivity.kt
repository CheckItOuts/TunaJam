package com.tunajam.app.playlist

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tunajam.app.data.FriendDirectory
import com.tunajam.app.firebase.Database
import com.tunajam.app.home.HomeActivity
import com.tunajam.app.spotify_login.SpotifyAPI
import com.tunajam.app.user_data.PlaylistData
import com.tunajam.app.user_data.UserData
import java.util.Locale


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
    val friendsList = FriendDirectory.friends.map { it.pseudo }
    var selectedFriends by remember { mutableStateOf(emptyList<String>()) }
    var maxAcousticness by remember { mutableFloatStateOf(0.5f) }
    var maxDanceability by remember { mutableFloatStateOf(0.5f) }
    var maxInstrulmentalness by remember { mutableFloatStateOf(0.5f) }
    var maxValence by remember { mutableFloatStateOf(0.5f) }
    var maxSpeechiness by remember { mutableFloatStateOf(0.5f) }
    var selectedGenres by remember { mutableStateOf(emptyList<String>()) }
    val genresList = listOf("acoustic", "afrobeat", "alt-rock",
        "alternative", "ambient", "anime", "black-metal", "bluegrass", "blues",
        "bossanova", "brazil", "breakbeat", "british", "cantopop", "chicago-house",
        "children", "chill", "classical", "club", "comedy", "country", "dance", "dancehall",
        "death-metal", "deep-house", "detroit-techno", "disco", "disney", "drum-and-bass", "dub",
        "dubstep", "edm", "electro", "electronic", "emo", "folk", "forro", "french", "funk",
        "garage", "german", "gospel", "goth", "grindcore", "groove", "grunge", "guitar", "happy",
        "hard-rock", "hardcore", "hardstyle", "heavy-metal", "hip-hop", "holidays", "honky-tonk",
        "house", "idm", "indian", "indie", "indie-pop", "industrial", "iranian", "j-dance", "j-idol",
        "j-pop", "j-rock", "jazz", "k-pop", "kids", "latin", "latino", "malay", "mandopop", "metal",
        "metal-misc", "metalcore", "minimal-techno", "movies", "mpb", "new-age", "new-release", "opera",
        "pagode", "party", "philippines-opm", "piano", "pop", "pop-film", "post-dubstep", "power-pop",
        "progressive-house", "psych-rock", "punk", "punk-rock", "r-n-b", "rainy-day", "reggae", "reggaeton",
        "road-trip", "rock", "rock-n-roll", "rockabilly", "romance", "sad", "salsa", "samba", "sertanejo",
        "show-tunes", "singer-songwriter", "ska", "sleep", "songwriter", "soul", "soundtracks", "spanish",
        "study", "summer", "swedish", "synth-pop", "tango", "techno", "trance", "trip-hop", "turkish",
        "work-out", "world-music")

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
                    .verticalScroll(rememberScrollState())
            ) {
                // Friend selection
                Text(
                    text = "Sélectionne tes amis : (5 maximums)",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 15.dp)
                )
                FriendSelection(
                    friendsList = friendsList,
                    selectedFriends = selectedFriends,
                    onFriendSelected = { friend ->
                        selectedFriends = selectedFriends.toMutableList().apply { add(friend) }
                    },
                    onFriendUnselected = { friend ->
                        selectedFriends = selectedFriends.filter { it != friend }
                    }
                )
                // Parameter sliders
                Text(
                    text = "Ajuste tes paramètres:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(bottom = 15.dp)
                        .align(CenterHorizontally)
                )
                ParameterSlider("Acoustique", maxAcousticness) {maxAcousticness = it }
                ParameterSlider("Dansant", maxDanceability) { maxDanceability = it }
                ParameterSlider("Instrumental", maxInstrulmentalness ) {maxInstrulmentalness = it }
                ParameterSlider("Positivité", maxValence) { maxValence = it }
                ParameterSlider("Paroles", maxSpeechiness ) {maxSpeechiness  = it }

                Text(text = "Genres musicaux : (sélectionne au moins un genre)",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 15.dp)
                )

                GenreSelection(
                    genresList = genresList,
                    selectedGenres = selectedGenres,
                    onGenreSelected = { genre ->
                        selectedGenres = selectedGenres.toMutableList().apply { add(genre) }
                    },
                    onGenreUnselected = { genre ->
                        selectedGenres = selectedGenres.filter { it != genre }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Generate playlist button
                Button(
                    onClick = {
                        if (selectedGenres.isNotEmpty() && selectedFriends.size in 0..5) {
                            generatePlaylist(
                                context,
                                selectedFriends,
                                maxAcousticness,
                                maxDanceability,
                                maxInstrulmentalness,
                                maxValence,
                                maxSpeechiness,
                                selectedGenres
                            )
                        } else {
                            // Afficher un message d'erreur à l'utilisateur
                            // Cela pourrait être un Toast, un Snackbar ou toute autre méthode que vous préférez
                            Toast.makeText(context, "Sélectionnez au moins 1 genre et entre 0 et 5 amis.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .align(CenterHorizontally)
                        .padding(top = 16.dp)
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

/**
 * Génère une playlist en fonction des paramètres donnés.
 */
fun generatePlaylist(context : Context, friends: List<String>, maxAcousticness: Float,
                     maxDanceability: Float, maxInstrulmentalness: Float,
                     maxValence: Float, maxSpeechiness: Float, selectedGenres: List<String>) {
    val accessToken = UserData.getAccessToken(context).toString()
    val refreshToken = UserData.getRefreshToken(context).toString()
    val genres = selectedGenres.toMutableList()
    val friendsList = friends.toMutableList()
    val seedMusic = mutableListOf<String>()
    val db = Database()

    for (friend in friendsList) {
        db.getLastMusic(friend) { music ->
           if (music != null) {
                seedMusic.add(music["id"].toString())
           }
       }
    }

    val parameters = mutableMapOf(
        "max_acousticness" to mutableListOf(maxAcousticness.toString()),
        "max_danceability" to mutableListOf(maxDanceability.toString()),
        "max_instrumentalness" to mutableListOf(maxInstrulmentalness.toString()),
        "max_valence" to mutableListOf(maxValence.toString()),
        "max_speechiness" to mutableListOf(maxSpeechiness.toString()),
        "seed_genres" to genres,
        "seed_tracks" to seedMusic
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


@Composable
fun GenreSelection(
    genresList: List<String>,
    selectedGenres: List<String>,
    onGenreSelected: (String) -> Unit,
    onGenreUnselected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        val visibleGenres = if (expanded) genresList else genresList.take(5) // Change 5 to desired initial number of visible items
        visibleGenres.forEach { genre ->
            val isChecked = selectedGenres.contains(genre)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            onGenreSelected(genre)
                        } else {
                            onGenreUnselected(genre)
                        }
                    }
                )
                Text(
                    text = genre.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        if (!expanded && genresList.size > 5) { // Change 5 to the desired threshold for expansion
            TextButton(onClick = { expanded = true }) {
                Text("Show More")
            }
        } else if (expanded) {
            TextButton(onClick = { expanded = false }) {
                Text("Show Less")
            }
        }
    }
}
@Composable
fun FriendSelection(
    friendsList: List<String>,
    selectedFriends: List<String>,
    onFriendSelected: (String) -> Unit,
    onFriendUnselected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCount by remember { mutableIntStateOf(selectedFriends.size) }

    Column {
        val visibleGenres = if (expanded) friendsList else friendsList.take(5)
        visibleGenres.forEach { genre ->
            val isChecked = selectedFriends.contains(genre)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = isChecked,
                    enabled = !isChecked || selectedCount > 1,
                    onCheckedChange = { isChecked ->
                        if (selectedCount < 5) {
                            onFriendSelected(genre)
                            selectedCount++
                        } else {
                            onFriendUnselected(genre)
                            selectedCount--
                        }
                    }
                )
                Text(
                    text = genre.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        if (!expanded && friendsList.size > 5) {
            TextButton(onClick = { expanded = true }) {
                Text("Show More")
            }
        } else if (expanded) {
            TextButton(onClick = { expanded = false }) {
                Text("Show Less")
            }
        }
    }
}