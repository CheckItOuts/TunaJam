package com.tunajam.app.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tunajam.app.data.FriendDirectory
import com.tunajam.app.data.PlaylistDirectory
import com.tunajam.app.data.SongDirectory
import com.tunajam.app.firebase.Database
import com.tunajam.app.model.TunaJamPhoto
import com.tunajam.app.playlist.PlaylistGenerationActivity
import com.tunajam.app.spotify_login.SpotifyAPI
import com.tunajam.app.ui.TunaJamTopAppBar
import com.tunajam.app.ui.screens.HomeScreen
import com.tunajam.app.ui.screens.TunaJamUiState
import com.tunajam.app.ui.screens.TunaJamViewModel
import com.tunajam.app.ui.theme.TunaJamBleuPale
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
        val db = Database()
        val pseudo = UserData.getUserName(this).toString()
        spotifyAPI.getUserPlaylist(this, accessToken, refreshToken) { playlists ->
            if (playlists != null) {
                PlaylistDirectory.clearPlaylists()
                for (i in 0 until playlists.size) {
                    val json = playlists[i]
                    val urlPlaylist = json.get("uri")
                    val imagesArr = json.get("images") as? JSONArray
                    val imageUrl = imagesArr?.optJSONObject(0)?.optString("url")
                    PlaylistDirectory.addPlaylist(
                        json.get("name").toString(),
                        json.get("id").toString()
                    )
                    imageUrl?.let {
                        val photo = TunaJamPhoto(json.get("id").toString(), it)
                        playlistPhotos.add(photo)
                    }
                }
            }
            db.getFriends(pseudo) {
                FriendDirectory.clearFriends()
                it.size
                for (friend in it) {
                    db.getUser(friend["friendPseudo"].toString()) { userData ->
                        if (userData != null) {
                            val friendName = userData["pseudo"].toString()
                            val friendPhotoUrl = userData["photo"].toString()
                            val friendPhoto = TunaJamPhoto(
                                friendName,
                                "https://pbs.twimg.com/profile_images/1611108206050250759/ORaNxrfb_400x400.jpg"
                            )
                            FriendDirectory.addFriend(friendName, friendName, true, friendPhoto)
                        }
                    }
                }
            }
            SpotifyAPI.getUserRecommendation(this, accessToken, refreshToken) { tracks ->
                SongDirectory.clearSongs()
                    if (tracks != null) {
                        for (i in 0 until tracks.size) {
                            val json = tracks[i]
                            val album = json.get("album") as JSONObject
                            val albumJson = album.get("images") as? JSONArray
                            val imageUrl = albumJson?.optJSONObject(0)?.optString("url")
                            val idSong = json.get("id").toString()
                            SongDirectory.addSong(
                                json.get("name").toString(),
                                (json.get("artists") as JSONArray).optJSONObject(0)
                                    ?.optString("name").toString(),
                                imageUrl.toString(), idSong
                            )
                            db.addMusic(pseudo, idSong)
                        }
                    }
                runOnUiThread {
                    setContent {
                        TunaJamTheme {
                            val tunaJamUiState = TunaJamUiState.Success(playlistPhotos)
                            Column {
                                HomeSetContent(tunaJamUiState, this@HomeActivity)
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
                title = { Text(text = "Home") }
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


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSetContent(tunaJamUiState: TunaJamUiState, context: Context) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val mediaPlayer = remember { MediaPlayer.create(context, com.tunajam.app.R.raw.slimyfishsound) }
    Scaffold(
        topBar = { TunaJamTopAppBar(scrollBehavior = scrollBehavior, context = context) },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                ElevatedButton(
                    onClick = { mediaPlayer.start()
                        navigateToPlaylistGenerationActivity(context)
                              },
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 12.dp),
                    colors = ButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        disabledContainerColor = TunaJamBleuPale,
                        disabledContentColor = Color.White
                    ),
                    shape= CircleShape
                ) {
                    Image(
                        painter = painterResource(id = com.tunajam.app.R.drawable.ic_launcher_round),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(130.dp)
                    )

                }

            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 65.dp)
        ) {
            item {
                val tunaJamViewModel: TunaJamViewModel =
                    viewModel(factory = TunaJamViewModel.Factory)
                HomeScreen(
                    tunaJamUiState = tunaJamUiState,
                    retryAction = tunaJamViewModel::getTunaJamPhotos,
                    contentPadding = it
                )
            }
        }
    }
}


fun navigateToPlaylistGenerationActivity(context: Context) {

    val intent = Intent(context, PlaylistGenerationActivity::class.java)
    context.startActivity(intent)
}

