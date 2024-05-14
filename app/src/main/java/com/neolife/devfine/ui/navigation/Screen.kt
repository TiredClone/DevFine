package com.neolife.devfine.ui.navigation

sealed class Screen(val route: String){
    data object HomePage: Screen("home_screen")
    data object MainPage: Screen("main_screen")
    data object AboutPage: Screen("about_screen")
    data object SettingsPage: Screen("settings_screen")
    data object AuthPage: Screen("auth_screen")
    data object SearchPage: Screen("search_screen")
}
