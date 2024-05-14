package com.neolife.devfine.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

object KtorInstance {
    private fun createHttpClient(): HttpClient {
        return HttpClient(OkHttp){
            engine {
                config {
                }
            }
        }
    }
}