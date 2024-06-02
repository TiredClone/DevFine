package com.neolife.devfine.di.core

import android.content.SharedPreferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SharedPrefManager : KoinComponent {
    private val sharedPreferences: SharedPreferences by inject()

    fun containsRefreshToken(): Boolean {
        return sharedPreferences.contains("refresh_token")
    }

    fun getRefreshToken(): String? {
        val refreshToken = sharedPreferences.getString("refresh_token", null)
        return refreshToken
    }

    fun saveRefreshToken(refreshToken: String, username: String) {
        sharedPreferences.edit().putString("refresh_token", refreshToken).apply()
        sharedPreferences.edit().putString("username", username).apply()
    }

    fun removeRefreshToken() {
        sharedPreferences.edit().remove("refresh_token").apply()
        sharedPreferences.edit().remove("username").apply()
    }

    fun getUsername(): String?{
        val username = sharedPreferences.getString("username", null)
        return username
    }

    fun getPostTitle(): String?{
        val postTitle = sharedPreferences.getString("title", null)
        return postTitle
    }
}