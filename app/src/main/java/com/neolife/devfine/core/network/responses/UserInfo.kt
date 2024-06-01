package com.neolife.devfine.core.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(val id: Int, val username: String, val role: String, val profilePicture: String)
