package com.neolife.devfine.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.halilibo.richtext.commonmark.CommonmarkAstNodeParser
import com.halilibo.richtext.commonmark.MarkdownParseOptions
import com.halilibo.richtext.markdown.BasicMarkdown
import com.halilibo.richtext.ui.RichTextStyle
import com.halilibo.richtext.ui.material3.RichText
import com.halilibo.richtext.ui.resolveDefaults
import com.halilibo.richtext.ui.string.RichTextStringStyle
import com.neolife.devfine.core.network.RequestHandler
import com.neolife.devfine.core.network.Utils
import com.neolife.devfine.core.network.responses.PostView
import com.neolife.devfine.di.core.SharedPrefManager
import com.neolife.devfine.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel
) {
    val color = when {
        isSystemInDarkTheme() -> Color.White
        else -> Color.Black
    }

    val colorBox = when {
        isSystemInDarkTheme() -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)
        else -> Color.White
    }
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullToRefreshState()
    var richTextStyle by remember { mutableStateOf(RichTextStyle(stringStyle = RichTextStringStyle(linkStyle = SpanStyle(color = color, textDecoration = TextDecoration.Underline))).resolveDefaults()) }

    var markdownParseOptions by remember { mutableStateOf(MarkdownParseOptions.Default) }
    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.getAllPosts()
            pullRefreshState.endRefresh()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar({
                OutlinedTextField(
                    modifier = Modifier
                        .padding(end = 15.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp)),
                    value = viewModel.query.value,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Black,
                        focusedBorderColor = Color.Black,
                        focusedContainerColor = Color.Black,
                        disabledBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                    ),
                    placeholder = { Text("Поиск", color = Color.Gray) },
                    onValueChange = { value ->
                        viewModel.query.value = value
                    })
            })
        }, contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .nestedScroll(pullRefreshState.nestedScrollConnection),
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
                    items(if (viewModel.query.value.text.isNotEmpty()) posts.toList().filter {
                        it.post.title.lowercase().contains(viewModel.query.value.text.lowercase())
                    } else posts.toList().asReversed(), key = { it.post.id }) { data ->
                        val content = Utils.parseMarkdown(data.post.content)
                        var expanded by remember { mutableStateOf(false) }
                        Box(
                            modifier = Modifier
                                .padding(5.dp)
                                .fillMaxWidth()
                                .clip(shape = RoundedCornerShape(15.dp))
                                .background(colorBox)
                                .clickable {
                                    navController.navigate(
                                        Screen.PostPage.route.replace(
                                            "{post_id}",
                                            data.post.id.toString()
                                        )
                                    )
                                }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .clickable {
                                            navController.navigate(
                                                Screen.ProfilePage.route.replace(
                                                    "{username}",
                                                    data.post.author?.username.toString()
                                                )
                                            )
                                        }
                                ) {
                                    AsyncImage(
                                        model = "https://devfine.tiredclone.me/api/users/images?filename=${data.post.author?.profilePicture}",
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .size(64.dp)
                                    )
                                    Column(
                                        modifier = Modifier
                                            .padding(start = 16.dp),
                                        verticalArrangement = Arrangement.Bottom,
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        data.post.author?.let {
                                            Text(
                                                modifier = Modifier.padding(top = 4.dp),
                                                text = it.username,
                                                fontSize = 20.sp,
                                                color = color,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                modifier = Modifier.padding(top = 4.dp),
                                                text = Utils.TimeOrDate(data.post.createdAt.toString()),
                                                fontSize = 15.sp
                                            )
                                        }
                                    }

                                    if (!viewModel.isLoading.value && (data.post.author?.username == SharedPrefManager().getUsername() || RequestHandler.role == "ADMIN")) {
                                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                            IconButton(
                                                onClick = {
                                                    expanded =
                                                        !expanded
                                                },
                                                modifier = Modifier.clickable(onClick = {
                                                    expanded =
                                                        !expanded
                                                })
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.MoreVert,
                                                    contentDescription = "More options"
                                                )
                                                DropdownMenu(
                                                    expanded = expanded,
                                                    onDismissRequest = {
                                                        expanded = false
                                                    },
                                                    modifier = Modifier.clickable(onClick = {
                                                        expanded = false
                                                    })
                                                ) {
                                                    DropdownMenuItem(
                                                        onClick = {
                                                            expanded = false
                                                            navController.navigate(
                                                                Screen.EditPostPage.route.replace(
                                                                    "{post_id}",
                                                                    data.post.id.toString()
                                                                )
                                                            )
                                                        },
                                                        text = { Text(text = "Изменить") })
                                                    DropdownMenuItem(
                                                        onClick = {
                                                            viewModel.deletePost(
                                                                data.post.id
                                                            )
                                                        },
                                                        text = { Text(text = "Удалить") })
                                                }
                                            }

                                        }
                                    }
                                }
                                Text(
                                    text = data.post.title,
                                    modifier = Modifier.padding(top = 16.dp),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 23.sp,
                                    color = color
                                )
                                Row(
                                    modifier = Modifier.padding(top = 16.dp)
                                ) {
                                    val parser = remember(markdownParseOptions) {
                                        CommonmarkAstNodeParser(markdownParseOptions)
                                    }
                                    val astNode = remember(parser) {
                                        parser.parse(content)
                                    }
                                    RichText(
                                        style = richTextStyle,
                                    ) {
                                        BasicMarkdown(astNode)
                                    }
                                }
                            }
                        }
                    }
                    item {
                        if (posts.isEmpty())
                            Text(text = "Записи отсутвуют")
                    }
                }
            }
            PullToRefreshContainer(
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }

}

class SearchViewModel : ViewModel() {
    var posts = MutableStateFlow<MutableList<PostView>>(mutableListOf())
    val isLoading = mutableStateOf(true)
    val query = mutableStateOf(TextFieldValue(""))

    init {
        getAllPosts()
    }

    fun deletePost(postId: Int) {
        viewModelScope.launch {
            val req = RequestHandler.removePost(postId)
            getAllPosts()
        }

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
