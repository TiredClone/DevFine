package com.neolife.devfine.core.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neolife.devfine.core.network.RequestHandler
import com.neolife.devfine.core.network.responses.PostView
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

    fun deleteComment(commentId: Int) {
        viewModelScope.launch {
            isUploadingComment.value = true
            RequestHandler.removeComment(commentId)
            refreshPost()
            isUploadingComment.value = false
        }
    }

    fun deletePost() {
        viewModelScope.launch {
            post.value?.post?.let {
                RequestHandler.removePost(it.id)
                isDeleted.value = true
            }
        }
    }
}