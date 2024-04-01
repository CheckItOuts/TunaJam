package com.tunajam.app.data

/**
 * Define a data class to represent a song
 */
data class Song(val title: String, val author: String, val url: String, val songUri : String)
object SongDirectory {
    val songs = mutableListOf<Song>()
    fun addSong(title: String, author: String, url: String,songUri :String) {
        val newSong = Song(title, author, url, songUri)
        songs.add(newSong)
    }
    fun clearSongs(){
        songs.clear()
    }
}