package com.tunajam.app.playlist

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tunajam.app.R
import com.tunajam.app.data.FriendDirectory
import com.tunajam.app.firebase.Database
import com.tunajam.app.home.HomeActivity
import com.tunajam.app.spotify_login.SpotifyAPI
import com.tunajam.app.ui.theme.TunaJamBleuPale
import com.tunajam.app.ui.theme.TunaJamViolet
import com.tunajam.app.ui.theme.Typography
import com.tunajam.app.user_data.PlaylistData
import com.tunajam.app.user_data.UserData
import java.util.Locale
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.Layout
import kotlin.math.min
import kotlin.math.roundToInt


class PlaylistGenerationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlaylistGenerationPage(onClickHome = {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }, context = this)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistGenerationPage(onClickHome: () -> Unit, context: Context) {
    val friendsList = FriendDirectory.friends.map { it.pseudo }
    var selectedFriends by remember { mutableStateOf(emptyList<String>()) }
    var maxAcousticness by remember { mutableFloatStateOf(0.5f) }
    var maxDanceability by remember { mutableFloatStateOf(0.5f) }
    var maxInstrulmentalness by remember { mutableFloatStateOf(0.5f) }
    var maxValence by remember { mutableFloatStateOf(0.5f) }
    var maxSpeechiness by remember { mutableFloatStateOf(0.5f) }
    var selectedGenres by remember { mutableStateOf(emptyList<String>()) }
    var searchQuery by remember { mutableStateOf("") }
    val genresList = listOf(

        "classical",
        "disco",
        "electro",
        "hip-hop", "jazz",
        "k-pop",
        "pop", "rock",
        "r-n-b",
        "techno", "acoustic",
        "afrobeat",
        "alt-rock",
        "alternative",
        "ambient",
        "anime",
        "black-metal",
        "bluegrass",
        "blues",
        "bossanova",
        "brazil",
        "breakbeat",
        "british",
        "cantopop",
        "chicago-house",
        "children",
        "club",
        "comedy",
        "country",
        "dancehall",
        "death-metal",
        "deep-house",
        "detroit-techno",
        "disney",
        "drum-and-bass",
        "dub",
        "dubstep",
        "edm",
        "electronic",
        "emo",
        "folk",
        "forro",
        "french",
        "funk",
        "garage",
        "german",
        "gospel",
        "goth",
        "grindcore",
        "groove",
        "grunge",
        "guitar",
        "happy",
        "hard-rock",
        "hardcore",
        "hardstyle",
        "heavy-metal",
        "holidays",
        "honky-tonk",
        "house",
        "idm",
        "indian",
        "indie",
        "indie-pop",
        "industrial",
        "iranian",
        "j-dance",
        "j-idol",
        "j-pop",
        "j-rock",
        "kids",
        "latin",
        "latino",
        "malay",
        "mandopop",
        "metal",
        "metal-misc",
        "metalcore",
        "minimal-techno",
        "movies",
        "mpb",
        "new-age",
        "new-release",
        "opera",
        "pagode",
        "philippines-opm",
        "piano",
        "pop-film",
        "post-dubstep",
        "power-pop",
        "progressive-house",
        "psych-rock",
        "punk",
        "punk-rock",
        "rainy-day",
        "reggae",
        "reggaeton",
        "road-trip",
        "rock-n-roll",
        "rockabilly",
        "romance",
        "sad",
        "salsa",
        "samba",
        "sertanejo",
        "show-tunes",
        "singer-songwriter",
        "ska",
        "songwriter",
        "soul",
        "soundtracks",
        "spanish",
        "swedish",
        "synth-pop",
        "tango",
        "trance",
        "trip-hop",
        "turkish",
        "world-music"
    )

    val eventsList = listOf("chill", "dance", "party", "sleep", "study", "summer", "workout")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Génération de playlist",
                        style = Typography.titleLarge
                    )
                }

            )
        },
        modifier = Modifier.padding(16.dp),
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
                    style = Typography.titleMedium,
                    modifier = Modifier
                        .padding(bottom = 15.dp)
                        .align(CenterHorizontally)
                )
                ParameterSlider("Acoustique", maxAcousticness) { maxAcousticness = it }
                ParameterSlider("Dansant", maxDanceability) { maxDanceability = it }
                ParameterSlider("Instrumental", maxInstrulmentalness) { maxInstrulmentalness = it }
                ParameterSlider("Positivité", maxValence) { maxValence = it }
                ParameterSlider("Paroles", maxSpeechiness) { maxSpeechiness = it }


                EventSelection(eventsList, onEventSelected = { event ->
                    selectedGenres = selectedGenres.toMutableList().apply {
                        add(event)
                    }
                    print("EventSelection: Selected genres: $selectedGenres")
                }, onEventUnselected = { event ->
                    selectedGenres = selectedGenres.filter { it != event }
                    print("EventSelection: Selected genres: $selectedGenres")
                })
                Text(
                    text = "Genres musicaux : (sélectionne au moins un genre)",
                    style = Typography.titleMedium,
                    modifier = Modifier.padding(top = 15.dp)
                )
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Rechercher un genre") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                )
                GenreSelection(
                    genresList = genresList.filter { it.contains(searchQuery, ignoreCase = true) },
                    selectedGenres = selectedGenres,
                    onGenreSelected = { genre ->
                        selectedGenres = selectedGenres.toMutableList().apply { add(genre) }
                        print("EventSelection: Selected genres: $selectedGenres")
                    },
                    onGenreUnselected = { genre ->
                        selectedGenres = selectedGenres.filter { it != genre }
                        print("EventSelection: Selected genres: $selectedGenres")
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            onClickHome()
                        },
                        colors = ButtonColors(
                            containerColor = TunaJamViolet,
                            contentColor = Color.White,
                            disabledContainerColor = TunaJamBleuPale,
                            disabledContentColor = Color.White
                        ),
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Go Back")
                        Text("Retour à l'accueil")
                    }
                    // Generate playlist button
                    Button(
                        onClick = {
                            if (selectedGenres.size in 1..5 && selectedFriends.size in 0..5) {
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
                                Toast.makeText(
                                    context,
                                    "Sélectionnez entre 1 et 5 genres et entre 0 et 5 amis.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        colors = ButtonColors(
                            containerColor =
                            TunaJamViolet,
                            contentColor = Color.White,
                            disabledContainerColor = TunaJamBleuPale,
                            disabledContentColor = Color.White
                        ),
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Generate Playlist")
                        Text("Générer la playlist")
                    }
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
            modifier = Modifier.fillMaxWidth(),
            colors = SliderColors(
                TunaJamBleuPale,
                TunaJamBleuPale,
                TunaJamBleuPale,
                Color.LightGray,
                Color.LightGray,
                Color.LightGray,
                Color.LightGray,
                Color.LightGray,
                Color.LightGray,
                Color.LightGray
            )
        )
    }
}

/**
 * Génère une playlist en fonction des paramètres donnés.
 */
fun generatePlaylist(
    context: Context, friends: List<String>, maxAcousticness: Float,
    maxDanceability: Float, maxInstrulmentalness: Float,
    maxValence: Float, maxSpeechiness: Float, selectedGenres: List<String>
) {
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
    SpotifyAPI.getGeneratedPlaylistTracks(
        context,
        accessToken,
        refreshToken,
        parameters
    ) { tracks ->
        println(tracks)
        if (tracks != null) {
            PlaylistData.savePlaylist(context, tracks)
            val intent = Intent(context, PlaylistDisplayActivity::class.java)
            context.startActivity(intent)
        }
    }
}

@Composable
fun EventSelection(
    eventsList: List<String>,
    onEventSelected: (String) -> Unit,
    onEventUnselected: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        for (i in 0..2) {
            EventItem(
                event = eventsList[i],
                onEventSelected = onEventSelected,
                onEventUnselected = onEventUnselected
            )
        }
    }
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        for (i in 3..4) {
            EventItem(
                event = eventsList[i],
                onEventSelected = onEventSelected,
                onEventUnselected = onEventUnselected
            )
        }
    }
}


@Composable
fun EventItem(
    event: String,
    onEventSelected: (String) -> Unit,
    onEventUnselected: (String) -> Unit,
) {
    var selected = remember { mutableStateOf(false) }
    val eventImageResourceId = getResourceIdByName(event)
    ElevatedButton(
        onClick = {
            selected.value = !selected.value
            if (selected.value) {
                onEventSelected(event)
            } else {
                onEventUnselected(event)
            }
        },
        modifier = Modifier.padding(5.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 12.dp),
        colors = ButtonColors(
            containerColor = if (selected.value) {
                TunaJamBleuPale
            } else {
                TunaJamViolet
            },
            contentColor = Color.White,
            disabledContainerColor = TunaJamBleuPale,
            disabledContentColor = Color.White
        ),
        contentPadding = PaddingValues(20.dp)
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            Image(
                painter = painterResource(id = eventImageResourceId),
                contentDescription = event + "Logo",

                modifier = Modifier
                    .width(65.dp)
                    .height(65.dp)
                    .clip(CircleShape)
            )
            Text(
                text = event,
                textAlign = TextAlign.Center,
                style = Typography.titleMedium
            )
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
    val columns = 2
    val rows = if (expanded) {(genresList.size + columns - 1) / columns} else 5


    Column (
    ){
        repeat(rows) { rowIndex ->
            Row(Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val start = rowIndex * columns
                val end = min(start + columns, genresList.size)
                for (i in start until end) {
                    val genre = genresList[i]
                    val isChecked = selectedGenres.contains(genre)
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                onGenreSelected(genre)
                            } else {
                                onGenreUnselected(genre)
                            }
                        },
                        colors = CheckboxColors(Color.White, Color.White, TunaJamViolet, Color.White,Color.White, Color.White, Color.White, TunaJamViolet,
                            TunaJamViolet, Color.White, Color.White, Color.White)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = genre.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                        style = MaterialTheme.typography.bodyMedium,
                        //modifier = Modifier.padding(start = 8.dp)
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        if (!expanded && genresList.size > 5) {
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
    private fun getResourceIdByName(resourceName: String): Int {
        val packageName = LocalContext.current.packageName
        val resClass = Class.forName("$packageName.R\$drawable")
        val resField = resClass.getField(resourceName.toLowerCase().replace("\\s+".toRegex(), "_"))
        return resField.getInt(null)
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

