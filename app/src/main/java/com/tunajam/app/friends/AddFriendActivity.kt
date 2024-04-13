package com.tunajam.app.friends

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
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
import com.tunajam.app.ui.TunaJamTopAppBar
import com.tunajam.app.ui.theme.TunaJamBeige
import com.tunajam.app.ui.theme.TunaJamViolet
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
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

        Scaffold(
            topBar = { TunaJamTopAppBar(scrollBehavior = scrollBehavior, context=context) },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 65.dp)
                        .padding(16.dp)
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
                    Text(
                        text="Attention aux minuscules/majuscules",
                        color = Color.Red
                    )
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        ElevatedButton(
                            onClick = {
                                navigateToFriendActivity(context, Modifier)
                            },
                            colors = ButtonColors(TunaJamBeige, Color.White, Color.Gray, Color.LightGray)

                        ) {
                            Text("Annuler")
                        }
                        ElevatedButton(
                            onClick = {
                                spotifyUsername = spotifyUsername.replace(" ", "")
                                if (spotifyUsername.isNotEmpty()) {
                                    // Vérifier si le pseudo existe dans la liste des utilisateurs
                                    val userExists = users.contains(spotifyUsername)
                                    val pseudo = UserData.getUserName(context)
                                    if (userExists && pseudo != spotifyUsername) {
                                        val db = Database()
                                        val photo = TunaJamPhoto(
                                            spotifyUsername,
                                            db.getFriendPhotoByUserCollection(spotifyUsername){friendPhotoUrl->
                                                if (friendPhotoUrl != null){
                                                    friendPhotoUrl
                                                } else{
                                                    Log.d("AddFriendActivity", ": Photo non trouvée")
                                                }
                                            }.toString()
                                        )
                                        FriendDirectory.addFriend(
                                            spotifyUsername,
                                            spotifyUsername,
                                            true,
                                            photo
                                        )
                                        db.addFriend(pseudo.toString(), spotifyUsername)
                                        onClickHome() // Retour à la page d'accueil
                                    } else {
                                        errorMessage = "L'utilisateur n'existe pas."
                                    }
                                } else {
                                    errorMessage = "Veuillez entrer un pseudo."
                                }
                            },
                            colors = ButtonColors(
                                TunaJamViolet,
                                Color.White,
                                Color.Gray,
                                Color.LightGray
                            )

                        ) {
                            Text("Ajouter")
                        }

                    }
                }
            }
        )
    }
}
fun navigateToFriendActivity(context: Context, modifier: Modifier.Companion){
    val intent = Intent(context, FriendsActivity::class.java)
    context.startActivity(intent)
}