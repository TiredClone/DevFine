package com.neolife.devfine.ui.pages


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.neolife.devfine.R
import com.neolife.devfine.core.network.RequestHandler
import com.neolife.devfine.core.network.Utils
import com.neolife.devfine.core.network.responses.PostView
import com.neolife.devfine.di.core.SharedPrefManager
import com.neolife.devfine.ui.navigation.Screen
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel, navController: NavController) {
    if (viewModel.goToAuth.value)
        navController.navigate(Screen.AuthPage.route)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                val inputStream = context.contentResolver.openInputStream(uri)
                val imageByteArray = inputStream?.readBytes()

                scope.launch {
                    RequestHandler.changeAvatar(imageByteArray)
                    navController.navigate(Screen.ProfilePage.route) {
                        popUpTo(Screen.ProfilePage.route) {
                            saveState = true
                        }
                    }
                }
            }
        }
    val pullRefreshState = rememberPullToRefreshState()
    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.loadingUserData()
            pullRefreshState.endRefresh()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.SettingsPage.route){
                        popUpTo(navController.graph.id)
                        {
                            inclusive = true
                        }
                    } }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                })
        }, contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .nestedScroll(pullRefreshState.nestedScrollConnection),

            ) {

            val color = when {
                isSystemInDarkTheme() -> Color.White
                else -> Color.Black
            }

            val colorBox = when {
                isSystemInDarkTheme() -> colorResource(R.color.cardColorBlack)
                else -> Color.White
            }


            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                item {
                    if (!viewModel.isLoading.value) {
                        AsyncImage(
                            model = viewModel.avatar.value,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(180.dp)
                                .clickable {
                                    launcher.launch("image/jpeg")
                                }
                                .border(
                                    2.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    CircleShape
                                )
                        )

                        Spacer(modifier = Modifier.size(20.dp))

                        Text(
                            text = viewModel.username.value,
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp
                        )

                        Spacer(modifier = Modifier.size(20.dp))
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
                                Text(
                                    text = data.post.title,
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
                                            imageVector = (if (data.votes.any { it.user?.username == SharedPrefManager().getUsername() }) {
                                                Icons.Filled.ThumbUp
                                            } else {
                                                Icons.Outlined.ThumbUp
                                            }), contentDescription = null
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
            PullToRefreshContainer(
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )


        }

    }
}



class ProfileViewModel : ViewModel() {
    var posts = MutableStateFlow<MutableList<PostView>>(mutableListOf())
    val username = mutableStateOf("")
    val avatar = mutableStateOf("")
    val isLoading = mutableStateOf(false)
    val id = mutableIntStateOf(0)
    val goToAuth = mutableStateOf(false)

    init {
        loadingUserData()
    }

    fun loadingUserData() {
        if (!SharedPrefManager().containsRefreshToken()) {
            goToAuth.value = true
            return
        }

        viewModelScope.launch {
            isLoading.value = true

            val infoReq =
                RequestHandler.getProfileByUsername(SharedPrefManager().getUsername().toString())

            if (infoReq == null) {
                goToAuth.value = true
            } else {
                username.value = infoReq.username
                avatar.value =
                    "https://devfine.tiredclone.me/api/users/images?filename=${infoReq.profilePicture}"

                id.intValue = infoReq.id

                posts.value = RequestHandler.getPostByAuthorId(infoReq.id)


                isLoading.value = false
            }

        }
    }


    fun updateAllPosts() {
        viewModelScope.launch {
            posts.value = RequestHandler.getPostByAuthorId(id.intValue)
        }
    }

    fun addLike(postId: Int){
        viewModelScope.launch {
            val findPostInList = posts.value.find { it.post.id == postId }
            val findUserInVotes = findPostInList!!.votes.any { it.user?.username == SharedPrefManager().getUsername()  }
            var likeStatus = 1
            if (findUserInVotes)
                likeStatus = 0
            RequestHandler.setLike(postId, likeStatus)
            updateAllPosts()
        }
    }
}
