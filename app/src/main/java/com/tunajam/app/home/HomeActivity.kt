package com.tunajam.app.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tunajam.app.R
import com.tunajam.app.model.TunaJamPhoto
import com.tunajam.app.playlist.PlaylistGenerationActivity
import com.tunajam.app.spotify_login.SpotifyAPI
import com.tunajam.app.ui.screens.HomeScreen
import com.tunajam.app.ui.screens.TunaJamUiState
import com.tunajam.app.ui.screens.TunaJamViewModel
import com.tunajam.app.ui.theme.TunaJamTheme
import com.tunajam.app.user_data.UserData
import org.json.JSONArray
import org.json.JSONObject


data class User(val name: String)

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val accessToken = UserData.getAccessToken(this).toString()
        val refreshToken = UserData.getRefreshToken(this).toString()
        val spotifyAPI = SpotifyAPI()
        val playlistPhotos = mutableListOf<TunaJamPhoto>()
        spotifyAPI.getUserPlaylist(this, accessToken, refreshToken) { playlists ->
            if (playlists != null) {
                for (i in 0 until playlists.size) {
                    val json = playlists[i]
                    // Afficher le nom de la playlist dans la console pour le moment
                    println("Playlist $i :")
                    println(json.get("name"))
                    val urlPlaylist = json.get("uri")
                    print(urlPlaylist)
                    val imagesArr = json.get("images") as? JSONArray
                    val imageUrl = imagesArr?.optJSONObject(0)?.optString("url")
                    imageUrl?.let {
                        val photo = TunaJamPhoto("$i", it)
                        playlistPhotos.add(photo)
                    }
                }
            }
            SpotifyAPI.getUserRecommendation(this, accessToken, refreshToken) { tracks ->
                spotifyAPI.getUserProfile(accessToken) { displayName, _, _, _ ->
                    val user = User(displayName.toString())
                    val parameters = mutableMapOf("seed_genres" to mutableListOf("rock"))
                    SpotifyAPI.getGeneratedPlaylistTracks(
                        this,
                        accessToken,
                        refreshToken,
                        parameters
                    ) { genTracks ->
                        runOnUiThread {
                            if (tracks != null) {
                                for (i in 0 until tracks.size) {
                                    val json = tracks[i]
                                    // Afficher le nom de la chanson dans la console pour le moment
                                    println("User Recommendation $i :")
                                    println(json.get("name"))
                                }
                            }
                            if (genTracks != null) {
                                for (i in 0 until genTracks.size) {
                                    val json = genTracks.get(i)
                                    // Afficher le nom de la chanson dans la console pour le moment
                                    println("Generated Playlist $i :")
                                    println(json.get("name"))
                                    val album = json.get("album") as JSONObject
                                    val imagesArr = album.get("images") as JSONArray
                                    val images = imagesArr.get(0) as JSONObject
                                    println(images.get("url"))
                                }
                            }
                            setContent {
                                TunaJamTheme {
                                    val tunaJamUiState = TunaJamUiState.Success(playlistPhotos)
                                    Column {
                                        TunaJamApp(tunaJamUiState)
                                    }
                                }
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
    fun UserInfo(user: User) {
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


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TunaJamApp(tunaJamUiState: TunaJamUiState) {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        Scaffold(
            // Je pense que c'est mieux de la laisser fixe
            //modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = { TunaJamTopAppBar(scrollBehavior = scrollBehavior) },
            bottomBar = { NavigationButton(onClick = {
                val intent = Intent(this, PlaylistGenerationActivity::class.java)
                startActivity(intent)
                finish() },
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .height(72.dp)
                    .fillMaxWidth()
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 65.dp)
            ) {
                val marsViewModel: TunaJamViewModel =
                    viewModel(factory = TunaJamViewModel.Factory)
                HomeScreen(
                    tunaJamUiState = tunaJamUiState,
                    retryAction = marsViewModel::getTunaJamPhotos,
                    contentPadding = it
                )

            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TunaJamTopAppBar(scrollBehavior: TopAppBarScrollBehavior, modifier: Modifier = Modifier) {
        CenterAlignedTopAppBar(
            scrollBehavior = scrollBehavior,
            title = {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall,
                )
            },
            modifier = modifier
        )
    }
}

@Composable
fun NavigationButton(
    onClick: () -> Unit,
    modifier: Modifier
) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .align(Alignment.Center) // Align the IconButton to the end of the Box
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_round),
                contentDescription = "App Logo",
                modifier = Modifier.width(72.dp).height(72.dp)
            )
        }
    }
}