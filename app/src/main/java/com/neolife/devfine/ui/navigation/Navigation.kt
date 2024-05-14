package com.neolife.devfine.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.neolife.devfine.ui.pages.AboutScreen
import com.neolife.devfine.ui.pages.AuthScreen
import com.neolife.devfine.ui.pages.HomeScreen
import com.neolife.devfine.ui.pages.MainScreen
import com.neolife.devfine.ui.pages.SearchScreen
import com.neolife.devfine.ui.pages.SettingsScreen

@Composable
fun HomeNavHost(modifier: Modifier,
                startDestination: String,
                navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = Screen.HomePage.route){
            HomeScreen(navController = navController)
        }
        composable(route = Screen.MainPage.route){
            MainScreen()
        }
        composable(route = Screen.AboutPage.route){
            AboutScreen(navController = navController)
        }
        composable(route = Screen.SettingsPage.route){
            SettingsScreen(navController = navController)
        }
        composable(route = Screen.AuthPage.route){
            AuthScreen(navController = navController)
        }

        composable(route = Screen.SearchPage.route){
            SearchScreen(navController = navController)
        }
    }
}