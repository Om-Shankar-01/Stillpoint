package com.example.stillpoint.data.network

import com.example.stillpoint.BuildConfig
import com.example.stillpoint.data.local.YouTubeResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YouTubeApiService @Inject constructor(
    private val client: HttpClient
) {
    suspend fun getVideoDetails(videoId: String): YouTubeResponse {
        val response = client.get("https://www.googleapis.com/youtube/v3/videos") {
            parameter("id", videoId)
            parameter("part", "snippet,contentDetails")
            parameter("key", BuildConfig.YOUTUBE_API_KEY)
        }

        return response.body()
    }

}