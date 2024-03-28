package com.tunajam.app.user_data

import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object PlaylistData {
    private const val PLAYLIST_KEY = "playlist"

    fun savePlaylist(context: Context, playlist: MutableList<JSONObject>) {
        val sharedPref = context.getSharedPreferences("playlist", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("playlist", playlist.toString())
            apply()
        }
    }

    fun getPlaylist(context: Context): MutableList<JSONObject> {
        val sharedPref = context.getSharedPreferences("playlist", Context.MODE_PRIVATE)
        val playlistData = mutableListOf<JSONObject>()
        val playlistString = sharedPref.getString(PLAYLIST_KEY, null)
        playlistString?.let {
            try {
                val jsonArray = JSONArray(it)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    playlistData.add(jsonObject)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return playlistData
    }
}