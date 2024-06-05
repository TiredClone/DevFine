package com.neolife.devfine.core.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class PostView(val post: Post, val comments: List<Comment>?, val votes: List<Vote>)

@Serializable
data class Post(val id: Int, val title: String, val content: String, val author: UserInfo?, val createdAt: String?, val updatedAt: String?)

@Serializable
data class PostCreate(val title: String, val content: String)

@Serializable
data class CommentCreate(val postId: Int, val parentComment: Int, val content: String)

@Serializable
data class Comment(val id: Int, val parentComment: String, val author: UserInfo?, val content: String, val createdAt: String?, val updatedAt: String? )

@Serializable
data class Vote(val id: Int, val user: UserInfo?, val vote: Int)

@Serializable
data class Like(val postId: Int, val like: Int)