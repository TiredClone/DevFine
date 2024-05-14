package com.neolife.devfine.core.network

import com.neolife.devfine.core.network.requests.LoginRequest
import com.neolife.devfine.core.network.requests.RefreshTokenRequest
import com.neolife.devfine.core.network.responses.LoginResponse
import com.neolife.devfine.core.network.responses.RefreshTokenResponse
import com.neolife.devfine.core.network.responses.UserInfo
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path
import kotlinx.serialization.SerializationException

object RequestHandler {
    private val client = KtorInstance.getHttpClient()

    private const val BASEURL = "192.168.100.30" //    DO NOT FORGET TO CHANGE IF PROD!

    private const val PORT = 8080 //    DO NOT FORGET TO CHANGE IF PROD

    private var accessToken = ""

    private val PROTOCOL = URLProtocol.HTTP //  DO NOT FORGET TO CHANGE IF PROD!

    suspend fun login(username: String, password: String): String? {

        val requestBody = LoginRequest(username, password)
        try {
            val req = client.post {
                url {
                    protocol = PROTOCOL
                    host = BASEURL
                    port = PORT
                    path("api/auth")
                }
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            val res: LoginResponse = req.body()

            this.accessToken = res.accessToken

            return res.refreshToken
        }
        catch (e: SerializationException) {
            return ""
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
                    port = PORT
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
                    port = PORT
                    path("api/auth/refresh")
                }
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            val res: RefreshTokenResponse = req.body()

            return res.token
        }
        catch (e: SerializationException) {
            return ""
        }
        catch (e: Exception) {
            return null
        }
    }
}