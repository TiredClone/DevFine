package com.neolife.devfine.ui.pages

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.neolife.devfine.core.network.RequestHandler
import com.neolife.devfine.core.network.responses.PostView
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(navController: NavController, viewModel: PostScreenViewModel, id: Int) {
    viewModel.postLoading(id)
    val color = when {
        isSystemInDarkTheme() -> Color.White
        else -> Color.Black
    }
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = "")
        }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(30.dp)
                )
            }
        })
    }){ innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ){
            if (viewModel.isLoading.value) {
                CircularProgressIndicator()
            } else {
                Text(text = viewModel.post.value!!.post.title, color = color, fontSize = 23.sp, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold )
                Text(text = viewModel.post.value!!.post.content, color = color, modifier = Modifier.padding(16.dp))
            }
        }
    }
}

class PostScreenViewModel : ViewModel() {
    var post: MutableState<PostView?> = mutableStateOf(null)
    val isLoading = mutableStateOf(true)
    val title = mutableStateOf("")
    val content = mutableStateOf("")
    val showFailedDialog = mutableStateOf(false)
    val dialogTitle = mutableStateOf("Error")
    val dialogCaption = mutableStateOf("Error")

    fun postLoading(id: Int){
        viewModelScope.launch {
            isLoading.value = true
            post.value = RequestHandler.getPostById(id)
            isLoading.value = false
        }
    }
}