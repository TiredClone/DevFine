package com.neolife.devfine.ui.components

import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.halilibo.richtext.commonmark.MarkdownParseOptions
import com.halilibo.richtext.ui.RichTextStyle
import com.halilibo.richtext.ui.resolveDefaults
import com.halilibo.richtext.ui.string.RichTextStringStyle
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

    val context = LocalContext.current
    var richTextStyle by remember { mutableStateOf(RichTextStyle(stringStyle = RichTextStringStyle(linkStyle = SpanStyle(color = color, textDecoration = TextDecoration.Underline))).resolveDefaults()) }
    var markdownParseOptions by remember { mutableStateOf(MarkdownParseOptions.Default) }
    Column(
        modifier = Modifier.padding(innerPadding),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,

        ) {
        if (viewModel.isLoading.value) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            )
            {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn()
            {
                item {
                    Row(
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(start = 16.dp)
                            .clickable {
                                navController.navigate(Screen.ProfilePage.route.replace(
                                    "{username}",
                                    viewModel.post.value?.post?.author?.username.toString()
                                ))
                            }
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
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = color
                                    )
                                }
                                Text(
                                    modifier = Modifier.padding(top = 4.dp),
                                    text = Utils.TimeOrDate(viewModel.post.value?.post?.createdAt.toString()),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                    viewModel.post.value?.post?.title?.let {
                        Text(
                            text = it.split(" ").take(15).joinToString(" ").take(100)+ "...",
                            color = color,
                            fontSize = 23.sp,
                            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }

//                    Row(
//                        modifier = Modifier.padding(top = 16.dp)
//                    ) {
//                        val parser = remember(markdownParseOptions) {
//                            CommonmarkAstNodeParser(markdownParseOptions)
//                        }
//                        val astNode = remember(parser) {
//                            parser.parse(viewModel.post.value!!.post.content)
//                        }
//                        RichText(
//                            style = richTextStyle,
//                            modifier = Modifier.padding(8.dp),
//                        ) {
//                            BasicMarkdown(astNode)
//                        }
//                    }

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
                item() {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
                    ) {

                            Text(
                                text = viewModel.post.value?.votes?.count().toString(),
                                fontSize = 20.sp
                            )
                            IconButton(onClick = {
                                if (SharedPrefManager().containsRefreshToken()) {
                                    viewModel.addLike(viewModel.post.value?.post?.id!!)
                                } else {
                                    Toast.makeText(context, "Вы не авторизованы", Toast.LENGTH_LONG)
                                        .show()
                                    navController.navigate(Screen.AuthPage.route)
                                }
                            }) {
                                Icon(
                                    imageVector = (if (viewModel.post.value?.votes!!.any { it.user?.username == SharedPrefManager().getUsername() }) {
                                        Icons.Filled.ThumbUp
                                    } else {
                                        Icons.Outlined.ThumbUp
                                    }), contentDescription = null
                                )
                            }


                        Text(
                            text = viewModel.post.value?.comments?.count().toString(),
                            fontSize = 20.sp,
                            modifier = Modifier.padding(end = 10.dp)
                        )
                        Icon(
                            imageVector = Icons.Filled.ChatBubbleOutline,
                            contentDescription = null
                        )
                    }

                }
                item {
                    HorizontalDivider()
                }
                item {
                    Text(
                        text = "Комментарии",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 18.sp,
                        color = color,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    HorizontalDivider()
                }
                viewModel.post.value?.comments?.let {
                    items(it.asReversed()) { data ->
                        var expanded by remember { mutableStateOf(false) }
                        if (!viewModel.isLoading.value && (RequestHandler.role == "ADMIN" || data.author!!.username == SharedPrefManager().getUsername())) {

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
                                            viewModel.isEditing.value = true
                                            viewModel.commentId.intValue = data.id
                                            viewModel.comment.value = TextFieldValue(data.content)
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
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
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
                                        .clickable {
                                            navController.navigate(Screen.ProfilePage.route.replace(
                                                "{username}",
                                                data.author.username
                                            ))
                                        }

                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expanded = true
                                })

                        HorizontalDivider(Modifier.padding(5.dp))
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

    if (viewModel.isEditing.value)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable {
                    viewModel.isEditing.value = false
                    viewModel.comment.value = TextFieldValue("")
                },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Нажми на меня чтобы отменить редактирование")
        }
}