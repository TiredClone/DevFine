package com.neolife.devfine.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.neolife.devfine.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text = "Аккаунт",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            })

        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TextButton(onClick = { navController.navigate(Screen.AuthPage.route) }) {
                Text(text = "Вход и регистрация")
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 40.dp, bottom = 40.dp)
            )
            TextButton(onClick = { navController.navigate(Screen.AboutPage.route) }) {
                Icon(imageVector = Icons.Filled.Info, contentDescription = "AboutApp" )
                Text(text = "О приложении")
            }
        }
    }
}