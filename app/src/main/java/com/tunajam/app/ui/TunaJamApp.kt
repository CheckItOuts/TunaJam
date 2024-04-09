@file:OptIn(ExperimentalMaterial3Api::class)

package com.tunajam.app.ui


import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.tunajam.app.R
import com.tunajam.app.friends.FriendsActivity
import com.tunajam.app.home.HomeActivity
import com.tunajam.app.model.TunaJamPhoto
import com.tunajam.app.ui.screens.HomeScreen
import com.tunajam.app.ui.screens.TunaJamViewModel
import com.tunajam.app.ui.theme.TunaJamBleuPale
import com.tunajam.app.ui.theme.TunaJamViolet
import com.tunajam.app.ui.theme.Typography
import com.tunajam.app.user.UserActivity
import com.tunajam.app.user_data.UserData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TunaJamApp(context: Context) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { TunaJamTopAppBar(scrollBehavior = scrollBehavior, context = context) }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            val marsViewModel: TunaJamViewModel =
                viewModel(factory = TunaJamViewModel.Factory)
            HomeScreen(
                tunaJamUiState = marsViewModel.tunaJamUiState,
                retryAction = marsViewModel::getTunaJamPhotos,
                contentPadding = it
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TunaJamTopAppBar(scrollBehavior: TopAppBarScrollBehavior, context:Context, modifier: Modifier = Modifier) {
    val pseudo = UserData.getUserName(context).toString()
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        colors = TopAppBarColors(
            containerColor = TunaJamViolet,
            scrolledContainerColor = TunaJamViolet,
            navigationIconContentColor = TunaJamBleuPale,
            titleContentColor = Color.White,
            actionIconContentColor = TunaJamBleuPale,
        ),
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = Typography.titleLarge,
                modifier = modifier.clickable {
                    navigateToHomePage(context = context)
                },
            )
        },
        navigationIcon = {
            Box(modifier = Modifier) {
                userProfileButton(userName = pseudo, context)
            }
        },

        actions = {
            Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                    friendListButton(context = context)
                }
        },
    )
}


@Composable
private fun userProfileButton(
    userName: String,
    context: Context
) {
    val photo= TunaJamPhoto(userName, UserData.getUserImgUrl(LocalContext.current).toString())
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable {
            navigateToUserProfile(context = context)
        }
    ) {

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
                .size(40.dp) // Taille de l'image
                .clip(CircleShape) // Clipping pour rendre l'image ronde
        )
        Text(
            text = userName,
            style = MaterialTheme.typography.titleSmall,
        )
    }

}

@Composable
private fun friendListButton(
    context: Context
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable {
            navigateToFriendPage(context = context)
        }
    ){
        Icon(Icons.Default.AccountBox, contentDescription = "Friend")
        Text(
            text = "Mes amis",
            style = MaterialTheme.typography.titleSmall,

        )
    }
}
fun navigateToUserProfile(context: Context) {
    val intent = Intent(context, UserActivity::class.java)
    context.startActivity(intent)
}
fun navigateToFriendPage(context: Context) {
    val intent = Intent(context, FriendsActivity::class.java)
    context.startActivity(intent)
}

fun navigateToHomePage(context: Context) {
    val intent = Intent(context, HomeActivity::class.java)
    context.startActivity(intent)
}

