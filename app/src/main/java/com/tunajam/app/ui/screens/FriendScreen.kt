package com.tunajam.app.ui.screens


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.tunajam.app.R
import com.tunajam.app.data.Friend
import com.tunajam.app.data.FriendDirectory
import com.tunajam.app.model.TunaJamPhoto

/**
 * Fetch les infos des friends depuis l'API Spotify
 */
@Composable
fun FriendScreen(
    tunaJamUiState: TunaJamUiState, retryAction: () -> Unit, modifier: Modifier = Modifier, contentPadding: PaddingValues = PaddingValues(0.dp),
    ) {
    // Songs recommendation
    //LoadSongRecommendationPanel()
    // User playlists grid
    println(FriendDirectory.friends)
    when (tunaJamUiState) {
        is TunaJamUiState.Loading -> LoadingScreen(modifier = Modifier.fillMaxWidth())
        is TunaJamUiState.Error -> ErrorScreen(retryAction, modifier = Modifier.fillMaxWidth())
        //TODO : à modifier pour récupérer les bonnes infos
        is TunaJamUiState.Success -> PhotosColumnScreen(FriendDirectory.friends, tunaJamUiState.photos, modifier.fillMaxWidth())
        else -> ErrorScreen(retryAction, modifier = Modifier.fillMaxWidth())
    }
}
@Composable
fun FriendCard(friend: Friend, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val deleteFriend = remember { mutableStateOf(false)}
    val photo = friend.picture

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        if (!deleteFriend.value) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier) {
                    // Dessine un cercle autour de l'image
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current).data(data = photo.imgSrc)
                                .apply(block = fun ImageRequest.Builder.() {
                                    crossfade(true)
                                }).build()
                        ),
                        contentDescription = stringResource(R.string.tunaJam_photo),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp) // Taille de l'image
                            .clip(CircleShape) // Clipping pour rendre l'image ronde
                    )
                    Canvas(
                        modifier = Modifier
                            .size(16.dp)
                            .padding(4.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        if(friend.isActive) {
                            drawCircle(Color.Green, radius = 13.dp.toPx())
                        }
                        else{
                            drawCircle(Color.Red, radius = 13.dp.toPx())
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(friend.pseudo)
                Spacer(modifier = Modifier.weight(1f)) //occupy the available space
                ElevatedButton(
                    onClick = {
                        deleteFriend.value = !deleteFriend.value
                    },

                    ) {
                    Text("remove")
                }
            }
        }
    }
}

@Composable
//charge les photos de playliste sous forme de colonne
fun PhotosColumnScreen(
    friends: List<Friend>,
    photos: List<TunaJamPhoto>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    println("Friends : $friends")
    LazyColumn(
        modifier = modifier.padding(horizontal = 4.dp).height(800.dp),
        contentPadding = contentPadding,
    ) {
        items(items = friends, key = { friend -> friend.id }) { friend ->
            FriendCard(
                friend,
                modifier = modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
            )
        }
    }
}