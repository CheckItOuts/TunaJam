package com.tunajam.app.data

/**
 * Define a data class to represent a playlist
 */
data class Playlist(val title: String, val id : String)
object PlaylistDirectory {
    val playlists = mutableListOf<Playlist>()
    fun addPlaylist(title: String, id: String) {
        val newPlaylist = Playlist(title, id)
        playlists.add(newPlaylist)
    }

    fun clearPlaylists(){
        playlists.clear()
    }
}