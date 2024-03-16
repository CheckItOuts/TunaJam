package com.tunajam.app.user_data

import android.content.Context

object UserData {
    private const val ACCESS_TOKEN = "accessToken"
    private const val REFRESH_TOKEN = "refreshToken"

    fun saveTokens(context: Context, accessToken: String, refresToken: String) {
        // On sauvegarde le token dans les préférences partagées
        val sharedPref = context.getSharedPreferences("spotify", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(ACCESS_TOKEN, accessToken)
            putString(REFRESH_TOKEN, refresToken)
            apply()
        }
    }

    fun getAccessToken(context: Context): String? {
        // On récupère le token dans les préférences partagées
        val sharedPref = context.getSharedPreferences("spotify", Context.MODE_PRIVATE)
        return sharedPref.getString(ACCESS_TOKEN, null)
    }

    fun getRefreshToken(context: Context): String? {
        // On récupère le token dans les préférences partagées
        val sharedPref = context.getSharedPreferences("spotify", Context.MODE_PRIVATE)
        return sharedPref.getString(REFRESH_TOKEN, null)
    }
}