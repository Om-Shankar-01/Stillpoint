package com.example.stillpoint.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "content_items")
data class ContentItem (
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val url: String,
    val title: String,
    val description: String?,
    val imageUrl: String?,
    val sourceName: String,
    val type: ContentType, // Either ARTICLE or VIDEO
    val estimatedTimeMinutes: Int,
    val addedAt: Long = System.currentTimeMillis(),
    val isArchived : Boolean = false,
    val cachedContent: String? = null,
    val languageCode: String? = "en"
)

enum class ContentType {
    ARTICLE,
    VIDEO
}