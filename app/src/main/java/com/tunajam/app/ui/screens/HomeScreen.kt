package com.tunajam.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tunajam.app.R
import com.tunajam.app.data.Playlist
import com.tunajam.app.data.PlaylistDirectory
import com.tunajam.app.data.Song
import com.tunajam.app.data.SongDirectory
import com.tunajam.app.model.TunaJamPhoto
import com.tunajam.app.spotify_login.SpotifyAPI
import com.tunajam.app.ui.theme.TunaJamBeige
import com.tunajam.app.ui.theme.TunaJamTheme
import com.tunajam.app.ui.theme.Typography
import com.tunajam.app.user_data.UserData


@Composable
fun HomeScreen(
    tunaJamUiState: TunaJamUiState,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    // Songs recommendation
    LoadSongRecommendationPanel()
    // User playlists grid
    when (tunaJamUiState) {
        is TunaJamUiState.Loading -> LoadingScreen(modifier = Modifier.fillMaxWidth())
        is TunaJamUiState.Error -> ErrorScreen(retryAction, modifier = Modifier.fillMaxWidth())
        is TunaJamUiState.Success -> PhotosGridScreen(
            tunaJamUiState.photos,
            modifier.fillMaxWidth()
        )

        else -> ErrorScreen(retryAction, modifier = Modifier.fillMaxWidth())
    }
}

/**
 * Displays the 3 recommendations of the day panel
 */
@Composable
fun LoadSongRecommendationPanel(
    songs: List<Song> = SongDirectory.songs,
    playlists: List<Playlist> = PlaylistDirectory.playlists
) {
    Text(
        text = "Mes Recommandations: (selon le goût de mes amis)",
        style = Typography.titleMedium,
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        songs.forEach { song ->
            DisplayRecomandedTitle(song, Modifier.weight(1f), playlists)
        }
        if (songs.isEmpty()) {
            Box(
                modifier = Modifier.background(TunaJamBeige) // Change the background color here
                    .padding(8.dp).clip(RoundedCornerShape(20.dp))
            ) {
                Text(
                    text = "Pas de chansons recommandées !",
                    style = Typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun DisplayRecomandedTitle(song: Song, modifier: Modifier, playlists: List<Playlist>) {
    val expanded = remember { mutableStateOf(false) }
    val removeSong = remember { mutableStateOf(false) }
    val extraPadding = if (expanded.value) 48.dp else 0.dp
    val imageNotListened = painterResource(R.drawable.songfished)
    val imageListened = painterResource(R.drawable.fishescaped)
    val imagePainter =
        rememberUpdatedState(if (removeSong.value) imageListened else imageNotListened)
    val accessToken = UserData.getAccessToken(LocalContext.current).toString()
    val refreshToken = UserData.getRefreshToken(LocalContext.current).toString()
    val spotifyAPI = SpotifyAPI()
    val context = LocalContext.current

    Surface(
        modifier = modifier.padding(vertical = 4.dp, horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .height(600.dp), // Set a fixed height for the cell

            /*.padding(top = 8.dp),*/
            contentAlignment = Alignment.TopStart, //center

        ) {
            Column(
                modifier = Modifier
                    .padding(bottom = extraPadding), verticalArrangement = Arrangement.Top
            ) {
                Image(
                    painter = imagePainter.value,
                    contentDescription = "The fished song illustration",
                    contentScale = ContentScale.Crop,
                )
                if (!removeSong.value) {
                    AsyncImage(model = ImageRequest.Builder(context = LocalContext.current)
                        .data(song.url)
                        .crossfade(true)
                        .build(),
                        error = painterResource(R.drawable.ic_broken_image),
                        placeholder = painterResource(R.drawable.loading_img),
                        contentDescription = stringResource(R.string.tunaJam_photo),
                        modifier = Modifier.clickable {
                            val intent = Intent(
                                Intent.ACTION_VIEW, Uri.parse(
                                    "https://open.spotify.com/track/" + song.songUri
                                )
                            )
                            context.startActivity(intent)
                        })
                    Text(text = song.title, modifier = modifier, textAlign = TextAlign.Center)
                    Text(text = song.author, modifier = modifier, textAlign = TextAlign.Center)

                    //add song
                    ElevatedButton(
                        onClick = {
                            expanded.value = !expanded.value
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally),

                        ) {
                        Text(if (expanded.value) "Ajouter " + song.title else "Ajouter à une playlist...")
                    }
                    //select a playlist
                    if (expanded.value) {
                        DropdownMenu(
                            expanded = expanded.value,
                            onDismissRequest = { expanded.value = false },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            for (playlist in playlists) {
                                DropdownMenuItem(
                                    text = { Text(playlist.title) },
                                    onClick = {
                                        spotifyAPI.addSongToPlaylist(
                                            context,
                                            accessToken,
                                            refreshToken,
                                            playlist.id,
                                            song.songUri,
                                            song.author,
                                            song.title
                                        ) {
                                            if (it) removeSong.value = true
                                        }

                                    }
                                )
                            }
                        }
                    }

                    //remove song
                    ElevatedButton(
                        onClick = {
                            removeSong.value = !removeSong.value
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        colors = ButtonDefaults.buttonColors(Color.Red)
                    ) {
                        Text(
                            if (!removeSong.value)
                                "Pas pour moi !"
                            else "Noté"
                        )
                    }
                }

            }
        }
    }
}


/**
 * The home screen displaying the loading message.
 */
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )
}

/**
 * The home screen displaying error message with re-attempt button.
 */
@Composable
fun ErrorScreen(retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text(stringResource(R.string.retry))
        }
    }
}

/**
 * ResultScreen displaying number of photos retrieved.
 */
@Composable
fun ResultScreen(photos: String, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(text = photos)
    }
}

@Composable
fun TunaJamPhotoCard(photo: TunaJamPhoto, modifier: Modifier = Modifier) {
    //formate les images format carré
    val context = LocalContext.current
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {

        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(photo.imgSrc)
                .crossfade(true)
                .build(),
            error = painterResource(R.drawable.ic_broken_image),
            placeholder = painterResource(R.drawable.loading_img),
            contentDescription = stringResource(R.string.tunaJam_photo),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .clickable {
                    val intent = Intent(
                        Intent.ACTION_VIEW, Uri.parse(
                            "https://open.spotify.com/playlist/" + photo.id
                        )
                    )
                    context.startActivity(intent)
                }
        )
    }
}

@Composable
//charge les photos de playliste sous forme de grille
fun PhotosGridScreen(
    photos: List<TunaJamPhoto>,
    modifier: Modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    contentPadding: PaddingValues = PaddingValues(0.dp)

) {
    Text(
        text = "Mon Contenu:",
        style = Typography.titleMedium,
    )
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            modifier = modifier
                .padding(horizontal = 4.dp)
                .height(800.dp),
            contentPadding = contentPadding,
        ) {
            items(items = photos, key = { photo -> photo.id }) { photo ->
                TunaJamPhotoCard(
                    photo,
                    modifier = modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                        .aspectRatio(1.5f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    TunaJamTheme {
        LoadingScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorScreenPreview() {
    TunaJamTheme {
        ErrorScreen({})
    }
}

@Preview(showBackground = true)
@Composable
fun PhotosGridScreenPreview() {
    TunaJamTheme {
        val mockData = List(10) { TunaJamPhoto("$it", "") }
        PhotosGridScreen(mockData, modifier = Modifier.fillMaxSize())
    }
}

