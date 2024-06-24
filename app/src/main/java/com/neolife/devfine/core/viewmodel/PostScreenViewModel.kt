package com.neolife.devfine.core.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.neolife.devfine.core.network.RequestHandler
import com.neolife.devfine.core.network.responses.PostView
import com.neolife.devfine.di.core.SharedPrefManager
import com.neolife.devfine.ui.navigation.Screen
import kotlinx.coroutines.launch

class PostScreenViewModel : ViewModel() {
    var post: MutableState<PostView?> = mutableStateOf(null)
    val postId = mutableIntStateOf(1)
    val comment = mutableStateOf(TextFieldValue())
    val isLoading = mutableStateOf(true)
    val isUploadingComment = mutableStateOf(false)
    val title = mutableStateOf("")
    val expanded = mutableStateOf(false)
    val content = mutableStateOf("")
    val isDeleted = mutableStateOf(false)
    val isEditing = mutableStateOf(false)
    val commentId = mutableIntStateOf(0)
    val showFailedDialog = mutableStateOf(false)
    val dialogTitle = mutableStateOf("Error")
    val dialogCaption = mutableStateOf("Error")

    fun postLoading(id: Int) {
        viewModelScope.launch {
            isLoading.value = true
            postId.intValue = id
            post.value = RequestHandler.getPostById(id)
            isLoading.value = false
        }
    }

    fun refreshPost() {
        viewModelScope.launch {
            post.value = RequestHandler.getPostById(postId.intValue)
        }
    }

    fun addComment() {
        if (comment.value.text.isNotEmpty()) {
            if (!isEditing.value)
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
                    refreshPost()
                }
            else
                viewModelScope.launch {
                    val tmpComment = comment.value.text
                    isEditing.value = false
                    isUploadingComment.value = true
                    RequestHandler.editComment(
                        commentId.intValue,
                        tmpComment
                    )
                    isUploadingComment.value = false
                    comment.value = TextFieldValue("")
                    refreshPost()
                }
        }
    }

    fun deleteComment(commentId: Int) {
        viewModelScope.launch {
            isUploadingComment.value = true
            RequestHandler.removeComment(commentId)
            refreshPost()
            isUploadingComment.value = false
        }
    }

    fun deletePost(postId: Int, navController: NavController) {
        viewModelScope.launch {
            isLoading.value = true
            isDeleted.value = true
            val req = RequestHandler.removePost(postId)
            navController.navigate(Screen.HomePage.route)
        }

    }

    fun updatePost(navController: NavController) {
        viewModelScope.launch {
            isLoading.value = true
            val req = RequestHandler.editPost(postId.intValue, title.value, content.value)
        }
        navController.navigate(Screen.PostPage.route)
        isLoading.value = false

    }

    fun addLike(postId: Int){
        viewModelScope.launch {
            val findUserInVotes = post.value?.votes?.any { it.user?.username == SharedPrefManager().getUsername()  }
            var likeStatus = 1
            if (findUserInVotes == true)
                likeStatus = 0
            RequestHandler.setLike(postId, likeStatus)
            refreshPost()
        }
    }

}