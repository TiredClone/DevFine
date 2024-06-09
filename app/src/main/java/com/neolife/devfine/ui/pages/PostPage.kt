package com.neolife.devfine.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.neolife.devfine.core.network.RequestHandler
import com.neolife.devfine.core.network.Utils
import com.neolife.devfine.core.network.responses.PostView
import com.neolife.devfine.di.core.SharedPrefManager
import com.neolife.devfine.ui.navigation.Screen
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(
    navController: NavController,
    viewModel: PostScreenViewModel,
    id: Int
) {
    viewModel.postLoading(id)
    val color = when {
        isSystemInDarkTheme() -> Color.White
        else -> Color.Black
    }

    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = "")
        },
            actions = {
                IconButton(
                    onClick = { viewModel.expanded.value = !viewModel.expanded.value },
                    modifier = Modifier.clickable(onClick = { viewModel.expanded.value = !viewModel.expanded.value })
                ) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More options")
                }
                DropdownMenu(
                    expanded = viewModel.expanded.value,
                    onDismissRequest = { viewModel.expanded.value = false },
                    modifier = Modifier.clickable(onClick = { viewModel.expanded.value = false })
                ) {
                    DropdownMenuItem(onClick = { /* Handle option 1 click */ }, text = { Text(text = "Изменить")})
                    DropdownMenuItem(onClick = { /* Handle option 1 click */ }, text = { Text(text = "Удалить")})
                }
            },
            navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(30.dp)
                )
            }
        })
    }, bottomBar = {
        OutlinedTextField(
            value = viewModel.comment.value,
            onValueChange = { viewModel.comment.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 15.dp),
            placeholder = { Text("Комментарий", color = Color.Gray) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { this.defaultKeyboardAction(ImeAction.Done); viewModel.addComment() }
            ),
        )

    }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,

        ) {
            if (viewModel.isLoading.value) {
                CircularProgressIndicator()
            } else {
                LazyColumn()
                {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp)
                        ) {
                            AsyncImage(
                                model = "https://devfine.tiredclone.me/api/users/images?filename=${viewModel.post.value!!.post.author?.profilePicture}",
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
                                viewModel.post.value?.post?.author.let {
                                    Text(
                                        modifier = Modifier.padding(top = 4.dp),
                                        text = it!!.username,
                                        fontSize = 20.sp
                                    )
                                    Text(
                                        modifier = Modifier.padding(top = 4.dp),
                                        text = Utils.TimeOrDate(viewModel.post.value!!.post.createdAt.toString()),
                                        fontSize = 20.sp
                                    )
                                }
                            }
                        }
                        Text(
                            text = viewModel.post.value!!.post.title,
                            color = color,
                            fontSize = 23.sp,
                            modifier = Modifier.padding(top = 16.dp, start = 16.dp),
                            fontWeight = FontWeight.Bold
                        )
                        MarkdownText(
                            modifier = Modifier.padding(16.dp),
                            markdown = viewModel.post.value!!.post.content,
                            style = TextStyle(
                                color = color,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Justify,
                            ),
                        )
                    }
                    item {
                        Text(text = "Комментарии")
                    }
                    viewModel.post.value?.comments?.let {
                        items(it.asReversed())  { data ->
                            var expanded by remember { mutableStateOf(false) }
                            Box(modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.TopEnd)) {

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    DropdownMenuItem(onClick = {  expanded = false }, text = { Text(text = "Изменить")})
                                    DropdownMenuItem(onClick = {  expanded = false }, text = { Text(text = "Удалить")})
                                }
                            }
                            ListItem(overlineContent = {
                                Text(
                                    text = data.author!!.username,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = color
                                )
                            },
                                headlineContent = {
                                    Text(
                                        text = Utils.TimeOrDate(data.createdAt.toString()),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = color
                                    )
                                },
                                supportingContent= {
                                    Text (
                                        text = data.content,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = color
                                    )
                                },

                                leadingContent = {
                                    AsyncImage(
                                        model = "https://devfine.tiredclone.me/api/users/images?filename=${data.author!!.profilePicture}",
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .size(40.dp)

                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expanded = true
                                    })
                        }
                    }
                }
            }
        }
        if (viewModel.isUploadingComment.value)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
    }
}

class PostScreenViewModel : ViewModel() {
    var post: MutableState<PostView?> = mutableStateOf(null)
    val postId = mutableIntStateOf(1)
    val comment = mutableStateOf(TextFieldValue())
    val isLoading = mutableStateOf(true)
    val isUploadingComment = mutableStateOf(false)
    val title = mutableStateOf("")
    val expanded = mutableStateOf(false)
    val content = mutableStateOf("")
    val showFailedDialog = mutableStateOf(false)
    val dialogTitle = mutableStateOf("Error")
    val dialogCaption = mutableStateOf("Error")

    fun postLoading(id: Int) {
        viewModelScope.launch {
            isLoading.value = true
            post.value = RequestHandler.getPostById(id)
            postId.intValue = id
            isLoading.value = false
        }
    }

    fun addComment() {
        if (comment.value.text.isNotEmpty())
        {
            viewModelScope.launch {
                isUploadingComment.value = true
                post.value?.post?.let {
                    RequestHandler.createComment(
                        it.id,
                        null,
                        comment.value.text
                    )
                }
                isUploadingComment.value = false
                comment.value = TextFieldValue("")
                postLoading(postId.intValue)
            }
        }
    }

}