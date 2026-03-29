package com.example.stillpoint.data.local

import kotlinx.serialization.Serializable

@Serializable
data class YouTubeResponse (
    val items : List<YouTubeVideoItem>
)

@Serializable
data class YouTubeVideoItem (
    val id: String,
    val snippet: YouTubeSnippet,
    val contentDetails: YouTubeContentDetails
)

@Serializable
data class YouTubeSnippet (
    val title: String,
    val channelTitle : String,
    val thumbnails: YouTubeThumbnails
)

@Serializable
data class YouTubeThumbnails (
    val high: YouTubeThumbnailDetails
)

@Serializable
data class YouTubeThumbnailDetails (
    val url: String
)

@Serializable
data class YouTubeContentDetails (
    val duration: String
)

