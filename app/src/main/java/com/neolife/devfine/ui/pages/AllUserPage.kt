package com.neolife.devfine.ui.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.neolife.devfine.core.network.RequestHandler
import com.neolife.devfine.core.network.responses.UserInfo
import com.neolife.devfine.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AllUserPage(navController: NavController, viewModel: AllUserViewModel,) {
    if (viewModel.goBack.value)
        navController.navigateUp()
    val users by viewModel.users.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullToRefreshState()
    val color = when {
        isSystemInDarkTheme() -> Color.White
        else -> Color.Black}
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text = "Список пользователи",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(30.dp),
                        )
                    }
                })
        }, contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .nestedScroll(pullRefreshState.nestedScrollConnection)
        ) {
            if (viewModel.isLoading.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn {
                    items(users.toList().asReversed()) { data ->
                        var expanded by remember { mutableStateOf(false) }
                        ListItem(overlineContent = {
                            Text(
                                text = data.username,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = color
                            )
                        },
                            headlineContent = {
                                Text(
                                    text = "ID: ${data.id}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },

                            leadingContent = {
                                AsyncImage(
                                    model = "https://devfine.tiredclone.me/api/users/images?filename=${data.profilePicture}",
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(40.dp)
                                )
                            },
                            modifier = Modifier.combinedClickable(
                                onClick = {
                                    navController.navigate(
                                        Screen.ProfilePage.route.replace(
                                            "{username}",
                                            data.username
                                        )
                                    )
                                },
                                onLongClick = {
                                    expanded = true
                                }
                            ),
                            trailingContent = {
                                if (data.role != "ADMIN")
                                    Icon(
                                        Icons.Filled.Close,
                                        modifier = Modifier.clickable {
                                            viewModel.selectedUser.value = data
                                            viewModel.showDialog.value = true
                                        },
                                        contentDescription = "Remove user"
                                    )
                            })

                    }
                }

            }
            if (viewModel.showDialog.value)
                AlertDialog(
                    onDismissRequest = { viewModel.showDialog.value = false },
                    title = { Text(text = "Предупреждение") },
                    text = {
                        Text(text = "Вы собираетесь удалить пользователя ${viewModel.selectedUser.value?.username}?")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.showDialog.value = false
                            viewModel.selectedUser.value?.let { viewModel.removeUser(it.id) }
                        }) {
                            Text(text = "Подтвердить")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            viewModel.showDialog.value = false
                        }) {
                            Text(text = "Отменить")
                        }
                    }
                )
        }
    }
}

class AllUserViewModel : ViewModel() {
    val users = MutableStateFlow<MutableList<UserInfo>>(mutableListOf())
    val isLoading = mutableStateOf(true)
    val showDialog = mutableStateOf(false)
    val goBack = mutableStateOf(false)
    val selectedUser: MutableState<UserInfo?> = mutableStateOf(null)
    init {
        getAllUsers()
    }

    fun removeUser(id: Int) {
        viewModelScope.launch {
            RequestHandler.removeUser(id)
            getAllUsers()
        }
    }

    fun getAllUsers() {
        viewModelScope.launch {
            val infoReq =
                RequestHandler.getAllUsers()
            users.value = infoReq
            isLoading.value = false
        }
    }
}
