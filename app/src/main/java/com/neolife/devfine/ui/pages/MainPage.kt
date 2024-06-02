package com.neolife.devfine.ui.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.neolife.devfine.di.core.SharedPrefManager
import com.neolife.devfine.ui.components.HomeBottomBar
import com.neolife.devfine.ui.navigation.BottomNavItem
import com.neolife.devfine.ui.navigation.HomeNavHost
import com.neolife.devfine.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint(
    "UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition",
    "UnrememberedMutableState"
)
@Composable
fun MainScreen() {
    val localNavController = rememberNavController()
    val startDestination = Screen.HomePage.route
    val TopLevelDestination = remember {
        listOf(
            BottomNavItem(
                title = "Лента",
                route = Screen.HomePage.route,
                icon = Icons.Outlined.Home
            ),
            BottomNavItem(
                title = "Поиск",
                route = Screen.SearchPage.route,
                icon = Icons.Filled.Search
            ),
            BottomNavItem(
                title = "Аккаунт",
                route = Screen.SettingsPage.route,
                icon = Icons.Filled.Person
            )
        )
    }

    val currentRoute = localNavController.currentBackStackEntryAsState().value?.destination?.route
    Scaffold(topBar = {
        if (currentRoute in TopLevelDestination.map { it.route }) {
            val currentInBottom = TopLevelDestination.find { it.route == currentRoute }
            TopAppBar(navigationIcon = {
                if (currentInBottom == null) {
                    IconButton(onClick = { localNavController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(30.dp))
                    }
                }
            }, title = {
                if (currentInBottom != null) {
                    Text(
                        text = currentInBottom.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 25.sp
                    )
                }
            }

            )
        }
    }, bottomBar = {
        if (currentRoute in TopLevelDestination.map { it.route }) {
            HomeBottomBar(
                destinations = TopLevelDestination,
                currentDestination = localNavController.currentBackStackEntryAsState().value?.destination
            ) {
                localNavController.navigate(it) {
                    popUpTo(localNavController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    restoreState = true
                }
            }
        }
    },
        floatingActionButton = {
            if (localNavController.currentBackStackEntryAsState().value?.destination?.route == Screen.HomePage.route
                && SharedPrefManager().containsRefreshToken()
            )
                FloatingActionButton(
                    onClick = { localNavController.navigate(Screen.CreatePostPage.route) } ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Extended floating action button"
                    )
                }
        }) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            HomeNavHost(
                startDestination = startDestination,
                navController = localNavController,
            )
        }
    }
}

