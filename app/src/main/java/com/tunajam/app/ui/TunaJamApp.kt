@file:OptIn(ExperimentalMaterial3Api::class)

package com.tunajam.app.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import com.tunajam.app.ui.screens.TunaJamViewModel


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tunajam.app.R
import com.tunajam.app.friends.FriendsActivity
import com.tunajam.app.home.HomeActivity
import com.tunajam.app.ui.screens.HomeScreen
import com.tunajam.app.user.UserActivity
import com.tunajam.app.user_data.UserData

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
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
                modifier = modifier.clickable {
                    navigateToHomePage(context = context)
                },
            )
        },

        actions = {
            Navigation(userName = pseudo, context)
        },
    )
}


@Composable
private fun Navigation(
    userName: String,
    context:Context
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = R.drawable.ic_broken_image), // Replace R.drawable.ic_user with your user icon
            contentDescription = "User Icon",
            modifier = Modifier.padding(end = 16.dp).clickable {
                navigateToUserProfile(context = context)
            }
        )
        Text(
            text = userName,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(end = 16.dp).clickable {
                navigateToUserProfile(context = context)
            }
        )
        Box(
            modifier = Modifier.padding(end = 16.dp).clickable {
                navigateToFriendPage(context = context)
            }
        ){
            androidx.compose.material3.Icon(Icons.Default.AccountBox, contentDescription = "Friend")
        }

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

