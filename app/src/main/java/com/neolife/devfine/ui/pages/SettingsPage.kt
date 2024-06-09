package com.neolife.devfine.ui.pages


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.neolife.devfine.core.network.RequestHandler
import com.neolife.devfine.di.core.SharedPrefManager
import com.neolife.devfine.ui.navigation.Screen
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel) {
    val color = when {
        isSystemInDarkTheme() -> Color.White
        else -> Color.Black
    }
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = "Настройки",
            fontWeight = FontWeight.Bold,
                color = color) })
    },  contentWindowInsets = WindowInsets(0.dp))
    {innerPadding ->

        if (viewModel.goToAuth.value)
            navController.navigate(Screen.AuthPage.route)

       LazyColumn(
           modifier = Modifier.fillMaxSize().padding(innerPadding)
       ) {
            item {
                if (!SharedPrefManager().containsRefreshToken()) {
                    ListItem(headlineContent = {
                        Text(
                            text = "Вход и регистрация",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = color
                        )
                    },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = "profile",
                                modifier = Modifier.size(30.dp),
                                tint = color
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(Screen.AuthPage.route)
                            })
                } else {
                    if (!viewModel.isLoading.value) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clickable { navController.navigate(Screen.ProfilePage.route) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                AsyncImage(
                                    model = viewModel.avatar.value,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .align(Alignment.CenterStart)
                                )

                                Text(
                                    text = viewModel.username.value,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(start = 80.dp),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = color
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
            item {
                if (SharedPrefManager().containsRefreshToken()) {
                    if (!viewModel.isLoading.value) {
                        ListItem(headlineContent = {
                            Text(
                                text = "Выйти из аккаунта",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = color
                            )
                        },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Outlined.Clear,
                                    contentDescription = "profile",
                                    modifier = Modifier.size(30.dp),
                                    tint = color
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    SharedPrefManager().removeRefreshToken()
                                    navController.navigate(Screen.SettingsPage.route)
                                })
                    }
                }
            }
            item {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
                )
            }
            item {
                ListItem(headlineContent = {
                    Text(
                        text = "Оформление",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = color
                    )
                },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.ColorLens,
                            contentDescription = "Theme",
                            modifier = Modifier.size(30.dp),
                            tint = color
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clickable {
                            navController.navigate(Screen.ThemePage.route)
                        }
                )
            }
            item {
                ListItem(headlineContent = {
                    Text(
                        text = "О приложении",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = color
                    )
                },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "AboutApp",
                            modifier = Modifier.size(30.dp),
                            tint = color
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate(Screen.AboutPage.route)
                       }
                )
           }
       }
   }
}


class SettingsViewModel : ViewModel() {
    val isLoading = mutableStateOf(false)
    val username = mutableStateOf("")
    val avatar = mutableStateOf("")
    val goToAuth = mutableStateOf(false)

    init {
        loadingUserData()
    }

    private fun loadingUserData() {

        viewModelScope.launch {
            if (SharedPrefManager().containsRefreshToken()) {
                isLoading.value = true

                val infoReq =
                    RequestHandler.getProfileByUsername(
                        SharedPrefManager().getUsername().toString()
                    )

                if (infoReq == null) {
                    goToAuth.value = true
                } else {
                    username.value = infoReq.username
                    avatar.value =
                        "https://devfine.tiredclone.me/api/users/images?filename=${infoReq.profilePicture}"

                    isLoading.value = false
                }
            }
        }
    }
}