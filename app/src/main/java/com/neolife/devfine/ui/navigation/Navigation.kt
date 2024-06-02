package com.neolife.devfine.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.neolife.devfine.ui.pages.AboutScreen
import com.neolife.devfine.ui.pages.AuthScreen
import com.neolife.devfine.ui.pages.HomeScreen
import com.neolife.devfine.ui.pages.MainScreen
import com.neolife.devfine.ui.pages.ProfileScreen
import com.neolife.devfine.ui.pages.RegisterScreen
import com.neolife.devfine.ui.pages.SearchScreen
import com.neolife.devfine.ui.pages.SettingsScreen
import com.neolife.devfine.ui.pages.ThemeScreen

@Composable
fun HomeNavHost(modifier: Modifier,
                startDestination: String,
                navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
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

        composable(route = Screen.SearchPage.route){
            SearchScreen(navController = navController)
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
    }
}