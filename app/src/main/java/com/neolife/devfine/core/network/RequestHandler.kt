package com.neolife.devfine.core.network

import com.neolife.devfine.core.network.requests.LoginRequest
import com.neolife.devfine.core.network.requests.RefreshTokenRequest
import com.neolife.devfine.core.network.responses.LoginResponse
import com.neolife.devfine.core.network.responses.RefreshTokenResponse
import com.neolife.devfine.core.network.responses.UserInfo
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path
import kotlinx.serialization.SerializationException

object RequestHandler {
    private val client = KtorInstance.getHttpClient()

    private const val BASEURL = "devfine.tiredclone.me" //    DO NOT FORGET TO CHANGE IF PROD!

    private var accessToken = ""

    private val PROTOCOL = URLProtocol.HTTPS //  DO NOT FORGET TO CHANGE IF PROD!

    suspend fun login(username: String, password: String): LoginResponse? {

        val requestBody = LoginRequest(username, password)
        try {
            val req = client.post {
                url {
                    protocol = PROTOCOL
                    host = BASEURL
                    path("api/auth")
                }
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            val res: LoginResponse = req.body()

            this.accessToken = res.accessToken

            return res
        }
        catch (e: Exception) {
            println(e.message)
            return null
        }
    }

    suspend fun registerUser(username: String, password: String): Boolean? {
        val requestBody = LoginRequest(username, password)
        try {
            val req = client.post {
                url {
                    protocol = PROTOCOL
                    host = BASEURL
                    path("api/users/register")
                }
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            val res: UserInfo = req.body()

            return true
        }
        catch (e: SerializationException) {
            return false
        }
        catch (e: Exception) {
            return null
        }
    }

    suspend fun refreshAccessToken(refreshToken: String): String? {
        val requestBody = RefreshTokenRequest(refreshToken)
        try {
            val req = client.post {
                url {
                    protocol = PROTOCOL
                    host = BASEURL
                    path("api/auth/refresh")
                }
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            val res: RefreshTokenResponse = req.body()

            accessToken = res.token

            return res.token
        }
        catch (e: SerializationException) {
            return ""
        }
        catch (e: Exception) {
            return null
        }
    }

    suspend fun getProfileByUsername(username: String): UserInfo? {
        try {
            val req = client.get {
                headers{
                    append("Authorization", "Bearer $accessToken")
                }
                url {
                    protocol = PROTOCOL
                    host = BASEURL
                    path("api/users/username/$username")
                }
                contentType(ContentType.Application.Json)
            }
            val res: UserInfo  = req.body()

            return res
        }
        catch (e: Exception) {
            return null
        }
    }
}