package com.neolife.devfine.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.neolife.devfine.core.network.RequestHandler
import com.neolife.devfine.core.network.Utils
import com.neolife.devfine.core.viewmodel.PostScreenViewModel
import com.neolife.devfine.di.core.SharedPrefManager
import com.neolife.devfine.ui.navigation.Screen
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun PostComponent(
    navController: NavController,
    viewModel: PostScreenViewModel,
    innerPadding: PaddingValues
) {
    val color = when {
        isSystemInDarkTheme() -> Color.White
        else -> Color.Black
    }
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
                            model = "https://devfine.tiredclone.me/api/users/images?filename=${viewModel.post.value?.post?.author?.profilePicture}",
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
                                it?.username?.let { it1 ->
                                    Text(
                                        modifier = Modifier.padding(top = 4.dp),
                                        text = it1,
                                        fontSize = 20.sp
                                    )
                                }
                                Text(
                                    modifier = Modifier.padding(top = 4.dp),
                                    text = Utils.TimeOrDate(viewModel.post.value?.post?.createdAt.toString()),
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                    viewModel.post.value?.post?.title?.let {
                        Text(
                            text = it,
                            color = color,
                            fontSize = 23.sp,
                            modifier = Modifier.padding(top = 16.dp, start = 16.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
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
                    items(it.asReversed()) { data ->
                        var expanded by remember { mutableStateOf(false) }
                        if (!viewModel.isLoading.value && (RequestHandler.role == "ADMIN" || viewModel.post.value?.post!!.author!!.username == SharedPrefManager().getUsername())) {

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentSize(Alignment.TopEnd)
                            ) {

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    DropdownMenuItem(
                                        onClick = {
                                            navController.navigate(
                                                Screen.CreatePostPage.route.replace("{post_id}",
                                                   viewModel.postId.toString()))
                                            expanded = false
                                        },
                                        text = { Text(text = "Изменить") })
                                    DropdownMenuItem(
                                        onClick = {
                                            viewModel.deleteComment(data.id)
                                            expanded = false
                                        },
                                        text = { Text(text = "Удалить") })
                                }
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
                            supportingContent = {
                                Text(
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