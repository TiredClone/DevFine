package com.neolife.devfine.ui.navigation

sealed class Screen(val route: String){
    data object HomePage: Screen("home_screen")
    data object MainPage: Screen("main_screen")
    data object AboutPage: Screen("about_screen")
    data object SettingsPage: Screen("settings_screen")
    data object AuthPage: Screen("auth_screen")
    data object SearchPage: Screen("search_screen")
    data object RegisterPage: Screen("register_screen")
    data object ProfilePage: Screen("profile_screen/{username}")
    data object ThemePage: Screen("theme_page")
    data object CreatePostPage: Screen("createPost_page")
    data object PostPage: Screen("post_page/{post_id}")
    data object AllUsersPage : Screen("all_users_page")
    data object  EditPostPage: Screen("editPost_page/{post_id}")
}
