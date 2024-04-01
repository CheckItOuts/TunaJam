package com.tunajam.app.firebase

import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.tunajam.app.firebase.Database as Database

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            testScreen()
        }
    }
}

@Composable
fun testScreen() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        testButton()
    }
}

@Composable
fun testButton() {
    val db = Database()
    Button(onClick = {
        db.addUser("test", "test")
        db.addUser("Louison", "test")
        db.addUser("Axel", "test")
        db.addFriend("Louison", "Axel")
        db.addFriend("Louison", "test")
        db.addMusic("Louison", "142563326")
        db.addMusic("Louison", "645946164")
        db.addMusic("Louison", "98613")
        db.getUser("Louison") { userData ->
            if (userData != null) {
                Log.d("", userData.toString())
            } else {
                Log.d("Problème user", "null")
            }
        }
        db.getFriends("Louison") { friendsData ->
            if (friendsData.isNotEmpty()) {
                Log.d("", friendsData.toString())
            } else {
                Log.d("Problème amis", "null")
            }
        }
        db.getLastMusic("Louison") { lastMusicData ->
            if (lastMusicData != null) {
                Log.d("", lastMusicData.toString())
            } else {
                Log.d("Problème music", "null")
            }
        }
        db.getUsers(){usersData ->
            if (usersData != null) {
                Log.d("", usersData.toString())
            }
            else{
                Log.d("Problem all users", "null")
            }
        }


    }) {
        Text(text = "Click Me")
    }
}
