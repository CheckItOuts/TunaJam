package com.tunajam.app.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tunajam.app.R
import com.tunajam.app.model.TunaJamPhoto
import com.tunajam.app.ui.theme.TunaJamTheme


@Composable
fun HomeScreen(
    tunaJamUiState: TunaJamUiState, retryAction: () -> Unit, modifier: Modifier = Modifier, contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    /*TODO : set up the 3 songs with the syntax SongDirectory.addSong("My First Song", "The First Author", "Axel")
       and set up the user playlists with the syntax PlaylistDirectory.addPlaylist("RoadTrip", mutableListOf<String>("Axel","Me", "Louison"))
    */
    //songs recommendation
    LoadSongRecommendationPanel()
    //user playlists grid
    when (tunaJamUiState) {
        is TunaJamUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is TunaJamUiState.Error -> ErrorScreen(retryAction, modifier = modifier.fillMaxSize())
        //Load playlist pictures
        is TunaJamUiState.Success -> PhotosGridScreen(tunaJamUiState.photos, modifier)
        else -> ErrorScreen(retryAction, modifier = modifier.fillMaxSize())
    }
}

/**
 * Define a data class to represent a song
 */
data class Song(val title: String, val author: String, val friend: String)
object SongDirectory {
    val songs = mutableListOf<Song>()
    fun addSong(title: String, author: String, friend: String) {
        val newSong = Song(title, author, friend)
        songs.add(newSong)
    }
}
//TODO : update the Playlist class (add its content, move the class definition...)
data class Playlist(val title: String, val contributors: List<String>)
object PlaylistDirectory {
    val playlists = mutableListOf<Playlist>()
    fun addPlaylist(title: String, contributors: List<String>) {
        val newPlaylist = Playlist(title, contributors)
        playlists.add(newPlaylist)
    }
}
/**
 * Displays the 3 recommendations of the day panel
 */
@Composable
fun LoadSongRecommendationPanel(songs: List<Song> = SongDirectory.songs, playlists: List<Playlist> = PlaylistDirectory.playlists){
    Row(modifier= Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)) {
        songs.forEach { song ->
            DisplayRecomandedTitle(song, Modifier.weight(1f), playlists)
        }
    }
}

@Composable
fun DisplayRecomandedTitle(song : Song, modifier : Modifier, playlists: List<Playlist>){
    val expanded = remember { mutableStateOf(false) }
    val removeSong = remember { mutableStateOf(false) }
    val extraPadding = if (expanded.value) 48.dp else 0.dp
    val imageNotListened = painterResource(R.drawable.songfished)
    val imageListened = painterResource(R.drawable.fishescaped)
    val imagePainter = rememberUpdatedState(if (removeSong.value) imageListened else imageNotListened)


    Surface(
        modifier = modifier//.padding(vertical = 4.dp, horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .height(600.dp), // Set a fixed height for the cell

            /*.padding(top = 8.dp),*/
            contentAlignment = Alignment.TopStart, //center

        ){
            Column(modifier = Modifier.padding(bottom = extraPadding), verticalArrangement = Arrangement.Top) {
                Image(
                    painter =imagePainter.value,
                    contentDescription = "The fished song illustration",
                    contentScale = ContentScale.Crop,
                )
                if (!removeSong.value){
                    Text(text = "Recommended by "+song.friend, color = Color.Gray,fontSize = 10.sp, lineHeight = 10.sp)
                    Text(text = song.title, modifier=modifier, textAlign = TextAlign.Center)
                    Text(text = song.author, modifier=modifier, textAlign = TextAlign.Center)

                    //add song
                    ElevatedButton(
                        onClick = {
                            expanded.value = !expanded.value
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally),

                        ) {
                        Text(if (expanded.value) "Adding "+song.title else "Add to a playlist...")
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
                                    text = { Text(playlist.title)},
                                    onClick = {
                                        //TODO ADD THE SONG :song TO THE PLAYLIST : playlist.title
                                        removeSong.value = true
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
                        Text(if (!removeSong.value)
                            "Not for me!"
                        else "Noted")
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
            modifier = Modifier.fillMaxWidth().padding(4.dp)
        )
    }
}

@Composable
//charge les photos de playliste sous forme de grille
fun PhotosGridScreen(
    photos: List<TunaJamPhoto>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = modifier.padding(horizontal = 4.dp),
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

