package com.tunajam.app.spotify_login

import android.content.Context
import com.tunajam.app.user_data.UserData
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

class SpotifyAPI {
    private val httpClient = OkHttpClient()

    fun getUserProfile(accessToken: String, callback: (String?, String?, String?, String?) -> Unit) {
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
                val imgUser = jsonObject.optString("images")
                callback(displayName, emailUser, idUser, imgUser)
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(null, null, null, null)
            }
        })
    }
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


    fun getUserTopTracks(context: Context, accessToken: String, refreshToken: String ,callback: (MutableList<String>?) -> Unit) {
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
                    refreshAccessToken(context,refreshToken) { newAccessToken ->
                        if (newAccessToken != null) {
                            // Retry the request with the new access token
                            getUserTopTracks(context,newAccessToken, refreshToken, callback)
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

    fun getUserTopArtists(context: Context,accessToken: String ,refreshToken: String , callback: (MutableList<String>?) -> Unit) {
    """Fonction pas encore totalement implémentée (il faut tester)"""
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me/top/artists?time_range=long_term&limit=5")
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
                    val artists: MutableList<String> = mutableListOf()
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

            override fun onFailure(call: Call, e: IOException) {
                if (e is SocketTimeoutException || e is ConnectException) {
                    // Access token expired, refresh it
                    refreshAccessToken(context,refreshToken) { newAccessToken ->
                        if (newAccessToken != null) {
                            // Retry the request with the new access token
                            getUserTopArtists(context,newAccessToken, refreshToken, callback)
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
                    // Handle failure
                    callback(null, null, null)
                }
            })
        }

        fun getUserRecommendation(context: Context, accessToken: String ,refreshToken: String ,callback: (MutableList<JSONObject>?) -> Unit) {
            val spotifyAPI = SpotifyAPI()
            spotifyAPI.getUserTopTracks(context,accessToken, refreshToken) { topTracks ->
                spotifyAPI.getUserTopArtists(context,accessToken, refreshToken) { topArtists ->
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
                                    println(item)
                                    recommendations.add(item)
                                }
                                callback(recommendations)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                callback(null)
                            }
                        }

                        override fun onFailure(call: Call, e: IOException) {
                            callback(null)
                        }
                    })
                }
            }
        }
    }
}