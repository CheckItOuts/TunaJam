package com.tunajam.app.user

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.tunajam.app.R
import com.tunajam.app.friends.AddFriendFloatingActionButton
import com.tunajam.app.friends.NavigationButton
import com.tunajam.app.friends.navigateToAddFriendActivity
import com.tunajam.app.home.navigateToPlaylistGenerationActivity
import com.tunajam.app.model.TunaJamPhoto
import com.tunajam.app.ui.TunaJamTopAppBar
import com.tunajam.app.ui.screens.TunaJamViewModel
import com.tunajam.app.ui.theme.TunaJamTheme
import com.tunajam.app.user_data.UserData


class UserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TunaJamTheme {
                ActivityLayout(this@UserActivity)
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPage(){
    // set val
    val wantsToDisconnect = remember {
        mutableStateOf(false)
    }
    val pseudo = UserData.getUserName(LocalContext.current).toString()
    val email = UserData.getUserEmail(LocalContext.current).toString()
    val img = UserData.getUserImgUrl(LocalContext.current).toString()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text="Votre Profil",
                    style = MaterialTheme.typography.headlineSmall)
                }

            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 65.dp).padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //profil
                //picture
                val photo=TunaJamPhoto(pseudo, img)
                SetUserPicture(photo, Modifier)
                // Pseudo
                Text(
                    text = pseudo,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 15.dp)
                )
                //mail
                Text(text = email,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 15.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                ElevatedButton(
                    onClick = {
                        wantsToDisconnect.value = !wantsToDisconnect.value
                    },

                    ) {
                    Text(if (!wantsToDisconnect.value)"Se déconnecter" else "Deconnexion...")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 65.dp)
                    ){
                    Text(text = "En ce moment votre chanson c'est :",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(top = 15.dp)
                    )
                    Text(text = "Le titre de ma chanson, L'Artiste.", //TODO : remplir avec la chanson du moment
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 15.dp)
                    )
                }
            }

        }
    )
}

@Composable
fun SetUserPicture(photo: TunaJamPhoto, modifier: Modifier){
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
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
                drawCircle(Color.Green, radius = 13.dp.toPx())
            }
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityLayout(context: Context) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = { TunaJamTopAppBar(scrollBehavior = scrollBehavior, context=context) },
        floatingActionButton = { AddFriendFloatingActionButton({ navigateToAddFriendActivity(context) }, modifier = Modifier) },
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
        val tunaJamViewModel: TunaJamViewModel =
            viewModel(factory = TunaJamViewModel.Factory)
        UserPage()
    }
}
