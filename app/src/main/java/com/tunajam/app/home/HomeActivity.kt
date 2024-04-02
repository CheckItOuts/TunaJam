package com.tunajam.app.home

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.foundation.lazy.LazyColumn
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
import com.tunajam.app.data.FriendDirectory
import com.tunajam.app.data.PlaylistDirectory
import com.tunajam.app.data.SongDirectory
import com.tunajam.app.firebase.Database
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
                    PlaylistDirectory.addPlaylist(json.get("name").toString(), json.get("id").toString())
                    imageUrl?.let {
                        val photo = TunaJamPhoto("$i", it)
                        playlistPhotos.add(photo)
                    }
                }
            }
            db.addUser("test8", "test8")
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
                runOnUiThread {
                    SongDirectory.clearSongs()
                    if (tracks != null) {
                        for (i in 0 until tracks.size) {
                            val json = tracks[i]
                            val album = json.get("album") as JSONObject
                            val albumJson = album.get("images") as? JSONArray
                            val imageUrl = albumJson?.optJSONObject(0)?.optString("url")
                            SongDirectory.addSong(json.get("name").toString(),
                                (json.get("artists") as JSONArray).optJSONObject(0)?.optString("name").toString(),
                                imageUrl.toString(),json.get("uri").toString())
                            val idSong = json.get("id").toString()
                            println(idSong)
                            db.addMusic(pseudo, idSong)
                        }
                    }
                    setContent {
                        TunaJamTheme {
                            val tunaJamUiState = TunaJamUiState.Success(playlistPhotos)
                            Column {
                                TunaJamApp(tunaJamUiState, this@HomeActivity)
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
fun TunaJamApp(tunaJamUiState: TunaJamUiState, context: Context) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = { TunaJamTopAppBar(scrollBehavior = scrollBehavior) },
        bottomBar = { NavigationButton(
                        onClick = { navigateToPlaylistGenerationActivity(context) },
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .height(72.dp)
                            .fillMaxWidth()
                    )
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
fun navigateToPlaylistGenerationActivity(context: Context) {
    val intent = Intent(context, PlaylistGenerationActivity::class.java)
    context.startActivity(intent)
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
                modifier = Modifier
                    .width(72.dp)
                    .height(72.dp)
            )
        }
    }
}