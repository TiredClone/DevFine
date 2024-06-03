package com.neolife.devfine.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel
) {

    val posts by viewModel.posts.collectAsStateWithLifecycle()
    if (viewModel.isLoading.value) {
        CircularProgressIndicator()
    } else {
        LazyColumn {
            item {
                TextField(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    value = viewModel.query.value,
//                    placeholder = {Text("пиши запрос уебан", color = MaterialTheme.colorScheme.surfaceDim)},
                    onValueChange = { value ->
                        viewModel.query.value = value
                    })
            }
            items(if (viewModel.query.value.text.isNotEmpty()) posts.toList().filter {
                it.title.lowercase().contains(viewModel.query.value.text.lowercase())
            } else posts.toList(), key={it.id}) { data ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable {
                            navController.navigate(
                                Screen.PostPage.route.replace(
                                    "{post_id}",
                                    data.id.toString()
                                )
                            )
                        }
                ){
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ){
                        Text(text = data.title)
                        Text(text = data.content)
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

class SearchViewModel : ViewModel() {
    var posts = MutableStateFlow<MutableList<Post>>(mutableListOf())
    val isLoading = mutableStateOf(true)
    val query =  mutableStateOf(TextFieldValue(""))
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
    var searchQuery by mutableStateOf("")
        private set
}
