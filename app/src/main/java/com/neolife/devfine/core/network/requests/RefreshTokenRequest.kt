package com.neolife.devfine.core.network.requests

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequest(val token: String)
