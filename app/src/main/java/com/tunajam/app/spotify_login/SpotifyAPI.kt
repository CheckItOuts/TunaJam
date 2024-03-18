package com.tunajam.app.spotify_login

import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class SpotifyAPI {
    private val httpClient = OkHttpClient()

    fun getUserProfile(accessToken: String, callback: (String?) -> Unit) {
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val jsonObject = JSONObject(responseBody.toString())
                val displayName = jsonObject.optString("display_name")
                callback(displayName)
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }
        })
    }

    fun refreshAccessToken(refreshToken: String, callback: (String?) -> Unit) {
        val requestBody = FormBody.Builder()
            .add("grant_type", "refresh_token")
            .add("refresh_token", refreshToken)
            .add("client_id", CLIENT_ID)
            .add("client_secret", CLIENT_SECRET)
            .build()

        val request = Request.Builder()
            .url("https://accounts.spotify.com/api/token")
            .post(requestBody)
            .build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val jsonObject = JSONObject(responseBody.toString())
                val newAccessToken = jsonObject.optString("access_token")
                callback(newAccessToken)
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }
        })
    }

    companion object {
        const val CLIENT_ID = "385d1740c16f4437b66802d5d0886d44"
        const val CLIENT_SECRET = "fd0de51ee127491fb6472f89bcd149d5"
        fun exchangeCodeForTokens(authorizationCode: String, callback: (String?, String?, String?) -> Unit) {
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
    }

}