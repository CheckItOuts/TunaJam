package com.tunajam.app.spotify_login

import android.content.Context
import com.google.gson.Gson
import com.tunajam.app.user_data.UserData
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

data class PlaylistData(val name: String, val description: String, val public: Boolean)

/**
 * Cette classe permet d'interagir avec l'API de Spotify.
 */
class SpotifyAPI {
    private val httpClient = OkHttpClient()

    /**
     * Cette fonction permet de récupérer le profil de l'utilisateur connecté.
     */
    fun getUserProfile(
        accessToken: String,
        context: Context,
        callback: (String?, String?, String?, String?) -> Unit
    ) {
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val jsonObject = JSONObject(responseBody.toString())
                val displayName = jsonObject.optString("display_name")
                val emailUser = jsonObject.optString("email")
                val idUser = jsonObject.optString("id")
                UserData.saveUserId(context, idUser)
                val imgUser = jsonObject.optString("images")
                val imgUserArray = JSONArray(imgUser)
                if (imgUserArray.length() == 0) {
                    callback(displayName, emailUser, idUser, null)
                    return
                }
                val imgUserObj = imgUserArray.getJSONObject(0)
                val imgUserUrl = imgUserObj.optString("url")
                callback(displayName, emailUser, idUser, imgUserUrl)
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(null, null, null, null)
            }
        })
    }

    /**
     * Cette fonction permet de rafraîchir le token d'accès (toutes les 3600 secondes).
     */
    fun refreshAccessToken(context: Context, refreshToken: String, callback: (String?) -> Unit) {
        val request = Request.Builder()
            .url("https://accounts.spotify.com/api/token?grant_type=refresh_token&refresh_token=$refreshToken")
            .addHeader("Authorization", "Basic <Base64 encoded client_id:client_secret>")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val jsonObject = JSONObject(responseBody!!)
                val accessToken = jsonObject.getString("access_token")
                UserData.saveTokens(context, accessToken, refreshToken)
                callback(accessToken)
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }
        })
    }

 /**
     * Cette fonction permet de récupérer les chansons les plus écoutées par l'utilisateur.
     */
    fun getUserTopTracks(
        context: Context,
        accessToken: String,
        refreshToken: String,
        callback: (MutableList<String>?) -> Unit
    ) {
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me/top/tracks?time_range=long_term&limit=5")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback(null)
                    return
                }
                val responseBody = response.body?.string()
                if (responseBody.isNullOrEmpty()) {
                    callback(null)
                    return
                }
                try {
                    val jsonObject = JSONObject(responseBody)
                    val res = jsonObject.getJSONArray("items")
                    val tracks: MutableList<String> = mutableListOf()
                    for (i in 0 until res.length()) {
                        val item = res.getJSONObject(i)
                        val track = item.get("id").toString()
                        tracks.add(track)
                    }
                    callback(tracks)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    callback(null)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                if (e is SocketTimeoutException || e is ConnectException) {
                    // Access token expired, refresh it
                    refreshAccessToken(context, refreshToken) { newAccessToken ->
                        if (newAccessToken != null) {
                            // Retry the request with the new access token
                            getUserTopTracks(context, newAccessToken, refreshToken, callback)
                        } else {
                            callback(null)
                        }
                    }
                } else {
                    callback(null)
                }
            }
        })
    }
 /**
     * Cette fonction permet de récupérer les artistes les plus écoutés par l'utilisateur.
  *    ATTENTION : Fonction pas encore totalement implémentée (il faut tester)
     */
    fun getUserTopArtists(
        context: Context,
        accessToken: String,
        refreshToken: String,
        callback: (MutableList<String>?) -> Unit
    ) {
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me/top/artists?time_range=long_term&limit=5")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                // On vérifie si la requête a réussi lors de la réponse
                if (!response.isSuccessful) {
                    callback(null)
                    return
                }
                val responseBody = response.body?.string()
                if (responseBody.isNullOrEmpty()) {
                    callback(null)
                    return
                }
                try {
                    // On récupère le JSON de la réponse
                    val jsonObject = JSONObject(responseBody)
                    val res = jsonObject.getJSONArray("items")
                    val artists: MutableList<String> = mutableListOf()
                    // On parcourt les artistes pour les ajouter à la liste
                    for (i in 0 until res.length()) {
                        val item = res.getJSONArray(i)
                        val artist = item.get(0).toString()
                        artists.add(artist)
                    }
                    callback(artists)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    callback(null)
                }
            }
            // En cas d'échec de la requête
            override fun onFailure(call: Call, e: IOException) {
                if (e is SocketTimeoutException || e is ConnectException) {
                    // Si le token d'accès a expiré, on le rafraîchit
                    refreshAccessToken(context, refreshToken) { newAccessToken ->
                        if (newAccessToken != null) {
                            // On réessaie la requête avec le nouveau token
                            getUserTopArtists(context, newAccessToken, refreshToken, callback)
                        } else {
                            callback(null)
                        }
                    }
                } else {
                    callback(null)
                }
            }
        })
    }


    companion object {
        const val CLIENT_ID = "385d1740c16f4437b66802d5d0886d44"
        const val CLIENT_SECRET = "fd0de51ee127491fb6472f89bcd149d5"
        /**
         * Cette fonction permet d'échanger le code d'authentification contre des tokens d'accès.
         */
        fun exchangeCodeForTokens(
            authorizationCode: String,
            callback: (String?, String?, String?) -> Unit
        ) {
            val formBodyBuilder = FormBody.Builder()
                .add("client_id", CLIENT_ID)
                .add("client_secret", CLIENT_SECRET)
                .add("redirect_uri", REDIRECT_URI)
                .add("code", authorizationCode)
                .add("grant_type", "authorization_code")


            val requestBody = formBodyBuilder.build()

            val request = Request.Builder()
                .url("https://accounts.spotify.com/api/token")
                .post(requestBody)
                .build()

            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    val jsonObject = JSONObject(responseBody.toString())
                    val accessToken = jsonObject.optString("access_token")
                    val refreshToken = jsonObject.optString("refresh_token")
                    val expiresIn = jsonObject.optInt("expires_in")
                    callback(accessToken, refreshToken, expiresIn.toString())
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback(null, null, null)
                }
            })
        }
/**
         * Cette fonction permet de récupérer les recommandations de chansons pour l'utilisateur.
         */
        fun getUserRecommendation(
            context: Context, accessToken: String,
            refreshToken: String,
            callback: (MutableList<JSONObject>?) -> Unit
        ) {
            val spotifyAPI = SpotifyAPI()
            spotifyAPI.getUserTopTracks(context, accessToken, refreshToken) { topTracks ->
                spotifyAPI.getUserTopArtists(context, accessToken, refreshToken) { topArtists ->
                    val seedTracks = topTracks?.joinToString(",")
                    val seedArtists = topArtists?.joinToString(",")

                    val request = Request.Builder()
                        .url("https://api.spotify.com/v1/recommendations?seed_tracks=$seedTracks&seed_artists=$seedArtists&limit=3")
                        .addHeader("Authorization", "Bearer $accessToken")
                        .build()

                    OkHttpClient().newCall(request).enqueue(object : Callback {
                        override fun onResponse(call: Call, response: Response) {
                            if (!response.isSuccessful) {
                                callback(null)
                                return
                            }
                            val responseBody = response.body?.string()
                            if (responseBody.isNullOrEmpty()) {
                                callback(null)
                                return
                            }
                            try {
                                val jsonObject = JSONObject(responseBody)
                                val res = jsonObject.getJSONArray("tracks")
                                val recommendations: MutableList<JSONObject> = mutableListOf()
                                for (i in 0 until res.length()) {
                                    val item = res.getJSONObject(i)
                                    recommendations.add(item)
                                }
                                callback(recommendations)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                callback(null)
                            }
                        }

                        override fun onFailure(call: Call, e: IOException) {
                            if (e is SocketTimeoutException || e is ConnectException) {
                                spotifyAPI.refreshAccessToken(
                                    context,
                                    refreshToken
                                ) { newAccessToken ->
                                    if (newAccessToken != null) {
                                        getUserRecommendation(
                                            context,
                                            newAccessToken,
                                            refreshToken,
                                            callback
                                        )
                                    } else {
                                        callback(null)
                                    }
                                }
                            } else {
                                callback(null)
                            }
                        }
                    })
                }
            }
        }
/**
         * Cette fonction permet de récupérer les chansons recommandées pour une playlist avec les paramètres pour la génération.
         */
        fun getGeneratedPlaylistTracks(
            context: Context, accessToken: String?, refreshToken: String,
            parameters: MutableMap<String, MutableList<String>>,
            callback: (MutableList<JSONObject>?) -> Unit
        ) {
            val urlParam = parameters.map { (key, value) -> "$key=${value.joinToString(",")}" }
                .joinToString("&")
            val request = Request.Builder()
                .url("https://api.spotify.com/v1/recommendations?$urlParam&limit=50")
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
            val spotifyAPI = SpotifyAPI()
            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        println("Error: ${response.code}")
                        callback(null)
                        return
                    }
                    val responseBody = response.body?.string()
                    if (responseBody.isNullOrEmpty()) {
                        println("Error: Empty response body")
                        callback(null)
                        return
                    }
                    try {
                        val jsonObject = JSONObject(responseBody)
                        val res = jsonObject.getJSONArray("tracks")
                        val recommendations: MutableList<JSONObject> = mutableListOf()
                        for (i in 0 until res.length()) {
                            val item = res.getJSONObject(i)
                            recommendations.add(item)
                        }
                        callback(recommendations)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        callback(null)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    if (e is SocketTimeoutException || e is ConnectException) {
                        spotifyAPI.refreshAccessToken(context, refreshToken) { newAccessToken ->
                            if (newAccessToken != null) {
                                getGeneratedPlaylistTracks(
                                    context,
                                    newAccessToken,
                                    refreshToken,
                                    parameters,
                                    callback
                                )
                            } else {
                                callback(null)
                            }
                        }
                    } else {
                        callback(null)
                    }
                }
            })
        }
/**
         * Cette fonction permet de créer une playlist sur Spotify avec les chansons passées en paramètre.
         */
        fun createPlaylist(
            context: Context, accessToken: String, refreshToken: String,
            userID: String, songs: MutableList<JSONObject>,
            name: String, description: String, callback: (String?) -> Unit
        ) {
            val spotifyAPI = SpotifyAPI()
            val playlistData = PlaylistData(name, description, false)
            val json = Gson().toJson(playlistData)
            val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder()
                .url("https://api.spotify.com/v1/users/$userID/playlists")
                .addHeader("Authorization", "Bearer $accessToken")
                .post(requestBody)
                .build()

            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    val jsonObject = JSONObject(responseBody!!)
                    val playlistID = jsonObject.getString("id")
                    val tracks = songs.map { it.getString("uri") }
                    val jsonArray = JSONArray(tracks)
                    val requestBody2 =
                        "{\"uris\":$jsonArray}".toRequestBody("application/json".toMediaTypeOrNull())

                    val request2 = Request.Builder()
                        .url("https://api.spotify.com/v1/playlists/$playlistID/tracks")
                        .addHeader("Authorization", "Bearer $accessToken")
                        .post(requestBody2)
                        .build()
                    OkHttpClient().newCall(request2).enqueue(object : Callback {
                        override fun onResponse(call: Call, response: Response) {
                            callback(playlistID)
                        }

                        override fun onFailure(call: Call, e: IOException) {
                            callback(null)
                        }
                    })
                }

                override fun onFailure(call: Call, e: IOException) {
                    if (e is SocketTimeoutException || e is ConnectException) {
                        spotifyAPI.refreshAccessToken(context, refreshToken) { newAccessToken ->
                            if (newAccessToken != null) {
                                createPlaylist(
                                    context,
                                    newAccessToken,
                                    refreshToken,
                                    userID,
                                    songs,
                                    name,
                                    description,
                                    callback
                                )
                            } else {
                                callback(null)
                            }
                        }
                    } else {
                        callback(null)
                    }
                }
            })
        }
    }
/**
     * Cette fonction permet de récupérer les playlists de l'utilisateur.
     */
    fun getUserPlaylist(
        context: Context,
        accessToken: String,
        refreshToken: String,
        callback: (MutableList<JSONObject>?) -> Unit
    ) {
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me/playlists")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback(null)
                    return
                }
                val responseBody = response.body?.string()
                if (responseBody.isNullOrEmpty()) {
                    callback(null)
                    return
                }
                try {
                    val jsonObject = JSONObject(responseBody)
                    val res = jsonObject.getJSONArray("items")
                    val playlists: MutableList<JSONObject> = mutableListOf()
                    for (i in 0 until res.length()) {
                        val item = res.getJSONObject(i)
                        playlists.add(item)
                    }
                    callback(playlists)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    callback(null)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                if (e is SocketTimeoutException || e is ConnectException) {
                    refreshAccessToken(context, refreshToken) { newAccessToken ->
                        if (newAccessToken != null) {
                            getUserPlaylist(context, newAccessToken, refreshToken, callback)
                        } else {
                            callback(null)
                        }
                    }
                } else {
                    callback(null)
                }
            }
        })
    }
    fun addSongToPlaylist(
        context: Context,
        accessToken: String,
        refreshToken: String,
        playlistID: String,
        songUri: String,
        callback: (Boolean) -> Unit
    ) {
        val tracks = JSONArray()
        tracks.put(songUri)
        val requestBody2 =
            "{\"uris\": $tracks}".toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/playlists/$playlistID/tracks")
            .addHeader("Authorization", "Bearer $accessToken")
            .post(requestBody2)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                println(response.body?.string())
                callback(response.isSuccessful)
            }

                override fun onFailure(call: Call, e: IOException) {
                if (e is SocketTimeoutException || e is ConnectException) {
                    refreshAccessToken(context, refreshToken) { newAccessToken ->
                        if (newAccessToken != null) {
                            addSongToPlaylist(context, newAccessToken, refreshToken, playlistID, songUri, callback)
                        } else {
                            callback(false)
                        }
                    }
                } else {
                    println(e)
                    callback(false)
                }
            }
        })
    }
}