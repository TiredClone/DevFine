package com.neolife.devfine.core.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class PostView(val post: Post)

@Serializable
data class Post(val id: Int, val title: String, val content: String, val author: UserInfo?, val createdAt: String?, val updatedAt: String?)

@Serializable
data class PostCreate(val title: String, val content: String)