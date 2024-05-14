package com.neolife.devfine.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.ui.unit.sp
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
                    fontSize = 25.sp
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
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "profile",
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = "Вход и регистрация",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 10.dp),
                    fontWeight = FontWeight.Medium
                )
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 30.dp, bottom = 30.dp)
            )
            TextButton(onClick = { navController.navigate(Screen.AboutPage.route) }) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "AboutApp",
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = "О приложении",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 10.dp),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}