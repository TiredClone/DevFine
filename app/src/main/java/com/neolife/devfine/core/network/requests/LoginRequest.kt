package com.neolife.devfine.core.network.requests

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val username: String, val password: String)