package com.tunajam.app.home

import android.annotation.SuppressLint
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tunajam.app.R
import com.tunajam.app.model.TunaJamPhoto
import com.tunajam.app.spotify_login.SpotifyAPI
import com.tunajam.app.ui.screens.TunaJamUiState
import com.tunajam.app.ui.theme.TunaJamTheme
import com.tunajam.app.user_data.UserData


data class User(val name: String)

class HomeActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val accessToken = UserData.getAccessToken(this).toString()
        val refreshToken = UserData.getRefreshToken(this).toString()
        val spotifyAPI = SpotifyAPI()
        SpotifyAPI.getUserRecommendation(this,accessToken,refreshToken) { tracks ->
            spotifyAPI.getUserProfile(accessToken) { displayName, _, _, _ ->
                val user = User(displayName.toString())
                val parameters = mutableMapOf("seed_genres" to mutableListOf("rock"))
                SpotifyAPI.getGeneratedPlaylistTracks(this, accessToken, refreshToken, parameters) {genTracks ->
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
                                    }
                                }
                                setContent {
                                    TunaJamTheme {
                                        val mockData = List(10) { TunaJamPhoto("$it", "") }
                                        val tunaJamUiState = TunaJamUiState.Success(mockData)
                                        HomeScreen(tunaJamUiState = tunaJamUiState, retryAction = { /*TODO*/ })
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


@Composable
fun HomeScreen(
    tunaJamUiState: TunaJamUiState, retryAction: () -> Unit, modifier: Modifier = Modifier, contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    when (tunaJamUiState) {
        is TunaJamUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is TunaJamUiState.Error -> ErrorScreen(retryAction, modifier = modifier.fillMaxSize())
        //Load playlist pictures
        is TunaJamUiState.Success -> PhotosGridScreen(tunaJamUiState.photos, modifier)

        else -> ErrorScreen(retryAction, modifier = modifier.fillMaxSize())
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
    Card(
        modifier = modifier,
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
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
//charge les photos de playliste sous forme de grille
fun PhotosGridScreen(
    photos: List<TunaJamPhoto>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
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
        PhotosGridScreen(mockData)
    }
}
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TunaJamTheme {
      UserInfo(user = User("John Doe"))
        }
    }
}