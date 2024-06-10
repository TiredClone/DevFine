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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.neolife.devfine.R
import com.neolife.devfine.core.network.Utils
import com.neolife.devfine.core.viewmodel.HomeViewModel
import com.neolife.devfine.di.core.SharedPrefManager
import com.neolife.devfine.ui.navigation.Screen
import dev.jeziellago.compose.markdowntext.MarkdownText


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsCard(
    viewModel: HomeViewModel,
    navController: NavController,
    innerPadding: PaddingValues,
) {
    val color = when {
        isSystemInDarkTheme() -> Color.White
        else -> Color.Black
    }

    val colorBox = when {
        isSystemInDarkTheme() -> colorResource(R.color.cardColorBlack)
        else -> Color.White
    }
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullToRefreshState()
    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.getAllPosts()
            pullRefreshState.endRefresh()
        }
    }
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
                if (!viewModel.isLoading.value) {
                    items(posts.toList().asReversed()) { data ->
                        val content = Utils.parseMarkdown(data.post.content)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
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
                                        .fillMaxWidth()
                                        .background(colorBox)
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
                                            .padding(start = 16.dp)
                                            .background(colorBox)
                                            .fillMaxSize(),
                                        verticalArrangement = Arrangement.Bottom,
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        data.post.author?.let {
                                            Text(
                                                modifier = Modifier.padding(top = 4.dp),
                                                text = it.username,
                                                fontSize = 20.sp
                                            )
                                            Text(
                                                modifier = Modifier.padding(top = 4.dp),
                                                text = Utils.TimeOrDate(data.post.createdAt.toString()),
                                                fontSize = 20.sp
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = data.post.title,
                                    modifier = Modifier.padding(top = 16.dp),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 23.sp,
                                    color = color,
                                )
                                MarkdownText(
                                    modifier = Modifier.padding(top = 16.dp),
                                    markdown = content,
                                    onClick = {
                                        navController.navigate(
                                            Screen.PostPage.route.replace(
                                                "{post_id}",
                                                data.post.id.toString()
                                            )
                                        )
                                    },
                                    style = TextStyle(
                                        color = color,
                                        fontSize = 18.sp,
                                        textAlign = TextAlign.Justify,
                                    ),
                                )
                                Row(
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(text = data.votes.count().toString(), fontSize = 24.sp)
                                    IconButton(onClick = { viewModel.addLike(data.post.id) }) {
                                        Icon(
                                            imageVector = if (data.votes.any { it.user?.username == SharedPrefManager().getUsername() }) {
                                                Icons.Filled.ThumbUp
                                            } else {
                                                Icons.Outlined.ThumbUp
                                            },
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    if (!viewModel.isLoading.value && posts.isEmpty())
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