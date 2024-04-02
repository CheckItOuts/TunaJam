package com.tunajam.app.friends

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tunajam.app.data.FriendDirectory
import com.tunajam.app.firebase.Database
import com.tunajam.app.model.TunaJamPhoto
import com.tunajam.app.user_data.UserData


class AddFriendActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Database()
        db.getUsers { usersList ->
            val users : MutableList<String> = mutableListOf()
            println(usersList)
            for (user in usersList) {
                println(user)
                user["pseudo"]?.let { users.add(it as String) }
            }
            runOnUiThread {
                setContent {
                    AddFriendPage(onClickHome = {
                        val intent = Intent(this, FriendsActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, users ,context = this)
                }
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddFriendPage(onClickHome: () -> Unit, users : List<String>,context: Context) {
        var spotifyUsername by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf("") }
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Ajoute un ami",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }

                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 65.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Friend selection
                    Text(
                        text = "Pseudo Spotify :",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 15.dp)
                    )
                    // Champ de saisie de texte
                    TextField(
                        value = spotifyUsername,
                        onValueChange = { spotifyUsername = it },
                        label = { Text("Entrez le pseudo Spotify") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 15.dp)
                    )
                    // Affichage du message d'erreur s'il existe
                    if (errorMessage.isNotEmpty()) {
                        Text(errorMessage, color = Color.Red)
                    }
                        ElevatedButton(
                        onClick = {
                            if (spotifyUsername.isNotEmpty()) {
                                // Vérifier si le pseudo existe dans la liste des utilisateurs
                                val userExists = users.contains(spotifyUsername)
                                val pseudo = UserData.getUserName(context)
                                if (userExists && pseudo != spotifyUsername) {
                                    val photo = TunaJamPhoto(spotifyUsername, "https://pbs.twimg.com/profile_images/1611108206050250759/ORaNxrfb_400x400.jpg")
                                    FriendDirectory.addFriend(spotifyUsername, spotifyUsername, true, photo)
                                    val db = Database()
                                    db.addFriend(pseudo.toString(), spotifyUsername)
                                    onClickHome() // Retour à la page d'accueil
                                } else {
                                    errorMessage = "L'utilisateur n'existe pas."
                                }
                            } else {
                                errorMessage = "Veuillez entrer un pseudo."
                            }

                        },

                        ) {
                        Text("Ajouter")
                    }

                }
            }
        )
    }
}