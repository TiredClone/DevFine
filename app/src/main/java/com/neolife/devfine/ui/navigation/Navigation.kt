package com.neolife.devfine.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.neolife.devfine.ui.pages.AboutScreen
import com.neolife.devfine.ui.pages.AuthScreen
import com.neolife.devfine.ui.pages.CreatePostScreen
import com.neolife.devfine.ui.pages.HomeScreen
import com.neolife.devfine.ui.pages.MainScreen
import com.neolife.devfine.ui.pages.PostScreen
import com.neolife.devfine.ui.pages.ProfileScreen
import com.neolife.devfine.ui.pages.RegisterScreen
import com.neolife.devfine.ui.pages.SearchScreen
import com.neolife.devfine.ui.pages.SettingsScreen
import com.neolife.devfine.ui.pages.ThemeScreen

@Composable
fun HomeNavHost(modifier: Modifier = Modifier,
                startDestination: String,
                navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable(route = Screen.HomePage.route){
            HomeScreen(navController = navController, viewModel = viewModel())
        }
        composable(route = Screen.MainPage.route){
            MainScreen()
        }
        composable(route = Screen.AboutPage.route){
            AboutScreen(navController = navController)
        }
        composable(route = Screen.SettingsPage.route){
            SettingsScreen(navController = navController, viewModel = viewModel())
        }
        composable(route = Screen.AuthPage.route){
            AuthScreen(viewModel = viewModel(), navController = navController)
        }

        composable(route = Screen.PostPage.route, arguments = listOf(navArgument("post_id") { type = NavType.IntType })) {
                backStackEntry ->
            backStackEntry.arguments?.getInt("post_id")
                ?.let { PostScreen(navController = navController, viewModel = viewModel(), id = it) }
        }

        composable(route = Screen.SearchPage.route){
            SearchScreen(navController = navController, viewModel = viewModel())
        }
        composable(route = Screen.RegisterPage.route){
            RegisterScreen(viewModel = viewModel(), navController = navController)
        }
        composable(route = Screen.ProfilePage.route){
            ProfileScreen(navController = navController , viewModel = viewModel())
        }

        composable(route = Screen.ThemePage.route){
            ThemeScreen(navController = navController)
        }
        composable(route = Screen.CreatePostPage.route){
            CreatePostScreen(navController = navController, viewModel = viewModel())
        }
    }
}