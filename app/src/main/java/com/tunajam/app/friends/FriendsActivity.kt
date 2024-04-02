package com.tunajam.app.friends

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
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tunajam.app.R
import com.tunajam.app.data.FriendDirectory
import com.tunajam.app.model.TunaJamPhoto
import com.tunajam.app.playlist.PlaylistGenerationActivity
import com.tunajam.app.spotify_login.SpotifyAPI
import com.tunajam.app.ui.screens.FriendScreen
import com.tunajam.app.ui.screens.TunaJamUiState
import com.tunajam.app.ui.screens.TunaJamViewModel
import com.tunajam.app.ui.theme.TunaJamTheme
import com.tunajam.app.user_data.UserData
import com.tunajam.app.friends.AddFriendActivity


data class User(val name: String)

class FriendsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val accessToken = UserData.getAccessToken(this).toString()
        val refreshToken = UserData.getRefreshToken(this).toString()
        val spotifyAPI = SpotifyAPI()
        val friendsPhotos = mutableListOf<TunaJamPhoto>()
        //TODO: spotifyAPI.getFriendsInfos(this, accessToken, refreshToken) {}
        //TODO:
        val friendPictureUrl = ""
        SpotifyAPI.getUserRecommendation(this, accessToken, refreshToken) { tracks ->
            spotifyAPI.getUserProfile(accessToken, this) { displayName, _, _, _ ->
                val user = User(displayName.toString())
                runOnUiThread {
                    FriendDirectory.clearFriends()
                    setContent {
                        TunaJamTheme {
                            val tunaJamUiState = TunaJamUiState.Success(friendsPhotos)
                            Column {
                                TunaJamApp(this@FriendsActivity)
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
                title = { Text(text = "Friends") }
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
fun TunaJamApp(context: Context) {
    val friendsPhotos = mutableListOf<TunaJamPhoto>()
    val tunaJamUiState = TunaJamUiState.Success(friendsPhotos)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = { TunaJamTopAppBar(scrollBehavior = scrollBehavior) },
        floatingActionButton = {AddFriendFloatingActionButton({navigateToAddFriendActivity(context)}, modifier = Modifier)},
        bottomBar = {
            NavigationButton(
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
                FriendScreen(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFriendFloatingActionButton(onClick: () -> Unit, modifier: Modifier){

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier
                .padding(bottom = 16.dp) // Adjust the bottom padding as needed
                .fillMaxWidth()
        ) {
            androidx.compose.material3.Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}

fun navigateToPlaylistGenerationActivity(context: Context) {
    val intent = Intent(context, PlaylistGenerationActivity::class.java)
    context.startActivity(intent)
}

fun navigateToAddFriendActivity(context: Context){
    val intent = Intent(context, AddFriendActivity::class.java)
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