package com.neolife.devfine.ui.pages

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.neolife.devfine.core.viewmodel.HomeViewModel
import com.neolife.devfine.ui.components.PostsCard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel, navController: NavController) {
    val color = when {
        isSystemInDarkTheme() -> Color.White
        else -> Color.Black}
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Лента", fontWeight = FontWeight.Bold, color = color) })
        }, contentWindowInsets = WindowInsets(0.dp)
    ) {innerPadding ->
       PostsCard(viewModel = viewModel , navController = navController, innerPadding = innerPadding)
    }
}

