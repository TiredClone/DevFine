package com.neolife.devfine.core.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neolife.devfine.core.network.RequestHandler
import com.neolife.devfine.core.network.responses.PostView
import com.neolife.devfine.di.core.SharedPrefManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    var posts = MutableStateFlow<MutableList<PostView>>(mutableListOf())
    val isLoading = mutableStateOf(true)

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


    fun updateAllPosts() {
        viewModelScope.launch {
            posts.value = RequestHandler.getAllPosts()
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