package com.neolife.devfine.core.network

import com.neolife.devfine.core.network.requests.LoginRequest
import com.neolife.devfine.core.network.requests.RefreshTokenRequest
import com.neolife.devfine.core.network.responses.Comment
import com.neolife.devfine.core.network.responses.CommentCreate
import com.neolife.devfine.core.network.responses.Like
import com.neolife.devfine.core.network.responses.LoginResponse
import com.neolife.devfine.core.network.responses.Post
import com.neolife.devfine.core.network.responses.PostCreate
import com.neolife.devfine.core.network.responses.PostView
import com.neolife.devfine.core.network.responses.RefreshTokenResponse
import com.neolife.devfine.core.network.responses.ReleaseResponse
import com.neolife.devfine.core.network.responses.UserInfo
import com.neolife.devfine.core.network.responses.Vote
import com.neolife.devfine.di.core.AppInfoManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

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

    suspend fun getUpdateInfoIfAvailable(): String? {
        val cl = HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        val req = cl.get("https://api.github.com/repos/TiredClone/DevFine/releases/latest")
        val res: ReleaseResponse = req.body()

        val oldVersion = AppInfoManager().getAppVersion()

        return if (oldVersion < res.name) {
            res.assets[0].browser_download_url
        } else {
            null
        }
    }

    suspend fun getAllPosts(): MutableList<PostView> {
        val req = client.get {
            url {
                protocol = PROTOCOL
                host = BASEURL
                path("api/posts/getAll")
            }
        }

        val res: MutableList<PostView> = req.body()

        return res
    }

    suspend fun getPostById(id: Int): PostView {
        val req = client.get {
            url {
                protocol = PROTOCOL
                host = BASEURL
                path("api/posts/$id")
            }
        }

        val res: PostView = req.body()

        return res
    }

    suspend fun getPostByAuthorId(id: Int): MutableList<PostView> {
        val req = client.get {
            url {
                protocol = PROTOCOL
                host = BASEURL
                path("api/posts/author/$id")
            }
        }

        val res: MutableList<PostView> = req.body()

        return res
    }

    suspend fun createPost(title: String, content: String): Int {
        val req = client.post {
            headers{
                append("Authorization", "Bearer $accessToken")
            }
            url {
                protocol = PROTOCOL
                host = BASEURL
                path("api/posts/create")
            }
            setBody(PostCreate(title, content))
            contentType(ContentType.Application.Json)
        }

        val res: Post = req.body()
        return res.id
    }


    suspend fun editPost(id: Int, title: String, content: String): Int {
        val req = client.put {
            headers{
                append("Authorization", "Bearer $accessToken")
            }
            url {
                protocol = PROTOCOL
                host = BASEURL
                path("api/posts/$id")
            }
            setBody(PostCreate(title, content))
            contentType(ContentType.Application.Json)
        }
        val res: Post = req.body()
        return res.id
    }

    suspend fun removePost(id: Int): Boolean {
        val req = client.delete {
            headers{
                append("Authorization", "Bearer $accessToken")
            }
            url {
                protocol = PROTOCOL
                host = BASEURL
                path("api/posts/$id")
            }
            contentType(ContentType.Application.Json)
        }
        return true
    }

    suspend fun changeAvatar(image: ByteArray?) {
        val req = client.post {
            headers{
                append("Authorization", "Bearer $accessToken")
            }
            url {
                protocol = PROTOCOL
                host = BASEURL
                path("api/users/changeAvatar")
            }
            setBody(
                MultiPartFormDataContent(
                    formData {
                        if (image != null) {
                            append("image", image, Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=\"image.jpg\"")
                            })
                        }
                    }
                )
            )

        }
    }

    suspend fun createComment(postId: Int, parentComment: Int?, content: String): Int {
        val req = client.post {
            headers{
                append("Authorization", "Bearer $accessToken")
            }
            url {
                protocol = PROTOCOL
                host = BASEURL
                path("api/comments/create")
            }
            setBody(CommentCreate(postId, parentComment, content))
            contentType(ContentType.Application.Json)
        }

        val res: Comment = req.body()
        return res.id
    }

    suspend fun getComment(postId: Int, parentComment: Int, content: String): Int {
        val req = client.get {
            headers{
                append("Authorization", "Bearer $accessToken")
            }
            url {
                protocol = PROTOCOL
                host = BASEURL
                path("api/comments/create")
            }
            setBody(CommentCreate(postId, parentComment, content))
            contentType(ContentType.Application.Json)
        }

        val res: Comment = req.body()
        return res.id
    }

    suspend fun setLike(postId: Int, like: Int): Int {
        val req = client.post {
            headers{
                append("Authorization", "Bearer $accessToken")
            }
            url {
                protocol = PROTOCOL
                host = BASEURL
                path("api/posts/like")
            }
            setBody(Like(postId, like))
            contentType(ContentType.Application.Json)
        }

        val res: Vote = req.body()
        return res.id
    }

}