package com.neolife.devfine.core.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenResponse(val token: String)
