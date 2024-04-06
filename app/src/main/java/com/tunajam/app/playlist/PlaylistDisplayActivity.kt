package com.tunajam.app.playlist

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.tunajam.app.user_data.PlaylistData
import org.json.JSONArray
import org.json.JSONObject
import androidx.compose.runtime.*
import com.tunajam.app.home.HomeActivity
import com.tunajam.app.spotify_login.SpotifyAPI
import com.tunajam.app.user_data.UserData

class PlaylistDisplayActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tracks = PlaylistData.getPlaylist(this)
        setContent {
            PlaylistDisplayPage(tracks)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PlaylistDisplayPage(playlistData: MutableList<JSONObject>) {
    val context = LocalContext.current
    var textAdd by remember { mutableStateOf("Ajouter à Spotify") }
    var playlistAdd = false
    val accessToken = UserData.getAccessToken(context).toString()
    val refreshToken = UserData.getRefreshToken(context).toString()
    val userId = UserData.getUserId(context).toString()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text="Playlist Générée",
                    style = MaterialTheme.typography.headlineSmall)}

            )
        },
        bottomBar = {
                    TopAppBar(title = {
                        Row {

                            Button(onClick = {val intent = Intent(context, HomeActivity::class.java)
                                context.startActivity(intent)
                            }) {
                                Text(text = "Retour à l'accueil")
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                onClick = {
                                    if (!playlistAdd) {
                                        SpotifyAPI.createPlaylist(
                                            context, accessToken, refreshToken, userId, playlistData,
                                            "Playlist générée par TunaJam", ""
                                        ) {playlistID ->
                                            if (playlistID != "") {
                                                playlistAdd = true
                                            }
                                        }
                                    }
                                    textAdd = if (textAdd == "Ajouter à Spotify") {
                                    "Ajouté !"
                                } else {
                                    "Ajouté !"
                                } },
                                modifier = Modifier.padding(end = 16.dp)
                            ) {
                                Text(text = textAdd)
                            }
                        }
                    })
        },
        content={
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 65.dp, bottom = 65.dp)
        ) {
            items(playlistData) { track ->
                TrackItem(track)
            }
        }
        }
    )
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun TrackItem(track: JSONObject) {
    val album = track.get("album") as JSONObject
    val imagesArr = album.get("images") as JSONArray
    val images = imagesArr.get(0) as JSONObject
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current).data(data = images.get("url").toString())
            .apply<ImageRequest.Builder>(block = fun ImageRequest.Builder.() {
                crossfade(true)
            }).build()
    )
    val context = LocalContext.current
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(track.get("uri").toString()))
                context.startActivity(intent)
            }
    ) {
        Image(
            painter = painter,
            contentDescription = "Track Image",
            modifier = Modifier.size(64.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = track.get("name").toString(), style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val playlistData = listOf(
        Pair("Track 1", "https://via.placeholder.com/150"),
        Pair("Track 2", "https://via.placeholder.com/150"),
        Pair("Track 3", "https://via.placeholder.com/150"),
    )
    //PlaylistScreen(playlistData)
}