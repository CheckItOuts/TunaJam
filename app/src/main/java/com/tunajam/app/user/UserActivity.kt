package com.tunajam.app.user

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.tunajam.app.R
import com.tunajam.app.firebase.Database
import com.tunajam.app.model.TunaJamPhoto
import com.tunajam.app.spotify_login.MainActivity
import com.tunajam.app.ui.TunaJamTopAppBar
import com.tunajam.app.ui.screens.TunaJamViewModel
import com.tunajam.app.ui.theme.TunaJamBleuPale
import com.tunajam.app.ui.theme.TunaJamTheme
import com.tunajam.app.ui.theme.Typography
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
    val db = Database()
    val lastSong = remember {mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        db.getLastMusic(pseudo) { music ->
            val songName = music?.get("name")?.toString() ?: "Unknown"
            val artistName = music?.get("artist")?.toString() ?: "Unknown"
            lastSong.value = "$songName - $artistName"
        }
    }
    val context = LocalContext.current
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
                    .padding(top = 65.dp)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //profil
                //picture
                val photo=TunaJamPhoto(pseudo, img)
                SetUserPicture(photo, Modifier)
                // Pseudo
                Text(
                    text = pseudo,
                    style = Typography.titleMedium,
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
                        UserData.clearUserData(context) // Clear user data
                        // Navigate back to login screen
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
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
                        style = Typography.titleMedium,
                        modifier = Modifier.padding(top = 15.dp)
                    )
                    Text( text = lastSong.value ?: "Chargement...",
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
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = CircleShape,
        border = BorderStroke(3.dp, TunaJamBleuPale),
        modifier = modifier.padding(top = 16.dp)
    ) {
        Box(modifier = modifier,
            contentAlignment = Alignment.Center) {

            // Photo de profil du user
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

    ) {
        val tunaJamViewModel: TunaJamViewModel =
            viewModel(factory = TunaJamViewModel.Factory)
        UserPage()
    }
}
