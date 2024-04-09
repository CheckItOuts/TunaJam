package com.tunajam.app.friends

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tunajam.app.model.TunaJamPhoto
import com.tunajam.app.ui.TunaJamTopAppBar
import com.tunajam.app.ui.screens.FriendScreen
import com.tunajam.app.ui.screens.TunaJamUiState
import com.tunajam.app.ui.screens.TunaJamViewModel
import com.tunajam.app.ui.theme.TunaJamBleuPale
import com.tunajam.app.ui.theme.TunaJamTheme


data class User(val name: String)

class FriendsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            runOnUiThread {
                setContent {
                    TunaJamTheme {

                        Column {
                            TunaJamApp(this@FriendsActivity)
                        }
                    }
                }
            }
        }
    }

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TunaJamApp(context: Context) {
    val friendsPhotos = mutableListOf<TunaJamPhoto>()
    val tunaJamUiState = TunaJamUiState.Success(friendsPhotos)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(

        topBar = { TunaJamTopAppBar(scrollBehavior = scrollBehavior, context=context) },
        floatingActionButton = {AddFriendFloatingActionButton({navigateToAddFriendActivity(context, Modifier)})},

    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 65.dp)
                .padding(16.dp)
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
fun AddFriendFloatingActionButton(onClick: () -> Unit, modifier : Modifier = Modifier) {

    Row(horizontalArrangement = Arrangement.Absolute.Center,
        verticalAlignment = Alignment.Bottom, modifier = modifier.padding(3.dp)) {
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier,
                //.fillMaxWidth().align(Alignment.CenterVertically),
            containerColor = TunaJamBleuPale // Assuming TunaJamBleuPale is your custom color
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add",
                tint = Color.White
            )
        }
    }
}

fun navigateToAddFriendActivity(context: Context, Modifier: Modifier.Companion){
    val intent = Intent(context, AddFriendActivity::class.java)
    context.startActivity(intent)
}

