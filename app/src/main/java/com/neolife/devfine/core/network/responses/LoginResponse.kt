package com.neolife.devfine.core.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(val accessToken: String, val refreshToken: String)
