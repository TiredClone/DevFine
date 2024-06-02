package com.neolife.devfine.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.neolife.devfine.core.network.RequestHandler
import com.neolife.devfine.core.network.responses.Post
import com.neolife.devfine.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel, navController: NavController) {
    Scaffold(
//        topBar = {
//            TopAppBar(title = {
//                Text(
//                    text = "Лента",
//                    fontWeight = FontWeight.SemiBold,
//                    fontSize = 25.sp
//                )
//            })
//        },
        floatingActionButton = {
            FloatingActionButton( onClick = { navController.navigate(Screen.CreatePostPage.route) } ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Extended floating action button"
                )
            }
        }
    ) { innerPadding ->

        val posts by viewModel.posts.collectAsStateWithLifecycle()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            if (viewModel.isLoading.value) {
                CircularProgressIndicator()
            } else {
                LazyColumn {
                    items(posts.toList()) { data ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clickable { navController.navigate(Screen.PostPage.route) }
                        ){
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ){
                                Text(text = "")
                            }
                        }
                    }
                    item {
                        if (posts.isEmpty())
                            Text(text = "Записи отсутвуют")
                    }
                }
            }
        }
    }
}

class HomeViewModel : ViewModel() {
    var posts = MutableStateFlow<MutableList<Post>>(mutableListOf())
    val isLoading = mutableStateOf(true)

    init {
        getAllPosts()
    }

    fun getAllPosts() {
        viewModelScope.launch {
            isLoading.value = true
            posts.value = mutableListOf()
            posts.value = RequestHandler.getAllPosts()
            isLoading.value = false
        }
    }
}