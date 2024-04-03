package com.tunajam.app.user_data

import android.content.Context

object UserData {
    private const val ACCESS_TOKEN = "accessToken"
    private const val REFRESH_TOKEN = "refreshToken"
    private const val USER_ID = "userId"
    private const val USER_NAME = "userName"
    private const val USER_EMAIL = "userEmail"
    private const val USER_IMG_URL = "userImgUrl"

    fun saveTokens(context: Context, accessToken: String, refresToken: String) {
        // On sauvegarde le token dans les préférences partagées
        val sharedPref = context.getSharedPreferences("spotify", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(ACCESS_TOKEN, accessToken)
            putString(REFRESH_TOKEN, refresToken)
            apply()
        }
    }

    fun saveUserId(context: Context, userId: String) {
        // On sauvegarde le token dans les préférences partagées
        val sharedPref = context.getSharedPreferences("spotify", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(USER_ID, userId)
            apply()
        }
    }

    fun saveUserName(context: Context, userName: String) {
        // On sauvegarde le token dans les préférences partagées
        val sharedPref = context.getSharedPreferences("spotify", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(USER_NAME, userName)
            apply()
        }
    }

    fun saveUserEmail(context: Context, userEmail: String) {
        // On sauvegarde le token dans les préférences partagées
        val sharedPref = context.getSharedPreferences("spotify", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(USER_EMAIL, userEmail)
            apply()
        }
    }

    fun saveUserImgUrl(context: Context, userImgUrl: String) {
        // On sauvegarde le token dans les préférences partagées
        val sharedPref = context.getSharedPreferences("spotify", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(USER_IMG_URL, userImgUrl)
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

    fun getUserId(context: Context): String? {
        // On récupère le token dans les préférences partagées
        val sharedPref = context.getSharedPreferences("spotify", Context.MODE_PRIVATE)
        return sharedPref.getString(USER_ID, null)
    }

    fun getUserName(context: Context): String? {
        // On récupère le token dans les préférences partagées
        val sharedPref = context.getSharedPreferences("spotify", Context.MODE_PRIVATE)
        return sharedPref.getString(USER_NAME, null)
    }

    fun getUserEmail(context: Context): String? {
        // On récupère le token dans les préférences partagées
        val sharedPref = context.getSharedPreferences("spotify", Context.MODE_PRIVATE)
        return sharedPref.getString(USER_EMAIL, null)
    }

    fun getUserImgUrl(context: Context): String? {
        // On récupère le token dans les préférences partagées
        val sharedPref = context.getSharedPreferences("spotify", Context.MODE_PRIVATE)
        return sharedPref.getString(USER_IMG_URL, null)
    }
}