package com.neolife.devfine.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.neolife.devfine.R
import com.neolife.devfine.core.network.RequestHandler
import com.neolife.devfine.core.network.responses.Post
import com.neolife.devfine.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel, navController: NavController) {
    val color = when {
        isSystemInDarkTheme() -> Color.White
        else -> Color.Black
    }

    val colorBox = when {
        isSystemInDarkTheme() -> colorResource(R.color.cardColorBlack)
        else -> Color.White
    }
    val posts by viewModel.posts.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Лента", fontWeight = FontWeight.Bold, color = color) })
        }, contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        )
        {
            if (viewModel.isLoading.value) {
                CircularProgressIndicator()
            } else {
                LazyColumn {
                    items(posts.toList()) { data ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                                .background(colorBox)
                                .clickable {
                                    navController.navigate(
                                        Screen.PostPage.route.replace(
                                            "{post_id}",
                                            data.id.toString()
                                        )
                                    )
                                }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = data.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 23.sp,
                                    color = color,
                                )
                                Text(text = data.content, color = color, fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp))
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