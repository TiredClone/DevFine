package com.neolife.devfine.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.neolife.devfine.core.network.RequestHandler
import com.neolife.devfine.core.viewmodel.PostScreenViewModel
import com.neolife.devfine.di.core.SharedPrefManager
import com.neolife.devfine.ui.components.PostComponent
import com.neolife.devfine.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(
    navController: NavController,
    viewModel: PostScreenViewModel,
    id: Int
) {
    if (!viewModel.isDeleted.value)
        viewModel.postLoading(id)
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = "")
        },
            actions = {

                if (!viewModel.isLoading.value && (viewModel.post.value?.post?.author?.username == SharedPrefManager().getUsername() || RequestHandler.role == "ADMIN")) {
                    IconButton(
                        onClick = { viewModel.expanded.value = !viewModel.expanded.value },
                        modifier = Modifier.clickable(onClick = {
                            viewModel.expanded.value = !viewModel.expanded.value
                        })
                    ) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = viewModel.expanded.value,
                        onDismissRequest = { viewModel.expanded.value = false },
                        modifier = Modifier.clickable(onClick = {
                            viewModel.expanded.value = false
                        })
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                navController.navigate(Screen.EditPostPage.route.replace(
                                    "{post_id}",
                                    viewModel.postId.intValue.toString()
                                ))
                            },
                            text = { Text(text = "Изменить") })
                        DropdownMenuItem(
                            onClick = {
                                viewModel.deletePost(viewModel.postId.intValue, navController)
                                navController.navigate(Screen.HomePage.route)
                            },
                            text = { Text(text = "Удалить") })
                    }
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
        if (SharedPrefManager().containsRefreshToken()) {
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
        }
    }) { innerPadding ->
        PostComponent(
            viewModel = viewModel, innerPadding = innerPadding, navController = navController
        )
    }
}
