package com.example.stillpoint.data

import android.util.Log
import com.example.stillpoint.data.local.ContentDao
import com.example.stillpoint.data.local.ContentItem
import com.example.stillpoint.data.local.ContentType
import com.example.stillpoint.data.network.YouTubeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.URL
import java.net.URLDecoder

data class ArticleContent(val title: String?, val body: String?)

interface ContentRepository {
    fun getAllItems(): Flow<List<ContentItem>>
    fun getArchivedItems(): Flow<List<ContentItem>>
    suspend fun saveContentFromUrl(url: String): Result<Unit>
    suspend fun deleteItem(item: ContentItem)
    suspend fun deleteMultipleItems(items: List<ContentItem>)
    suspend fun archiveItem(item: ContentItem)
    suspend fun unarchiveItem(item: ContentItem)
    suspend fun unarchiveMultipleItems(items: List<ContentItem>)
    suspend fun getArticleContent(url: String): Result<ArticleContent>
}

class CachingContentRepository(
    private val contentDao: ContentDao,
    private val webContentExtractor: WebContentExtractor,
    private val youTubeApiService: YouTubeApiService
) : ContentRepository {

    private fun extractYouTubeId(url: String): String? {
        val decodedUrl = URLDecoder.decode(url, "UTF-8")
        val regex = Regex(
            "(?:youtube\\.com/(?:watch\\?v=|embed/|v/|shorts/)|youtu\\.be/)([a-zA-Z0-9_-]{11})"
        )
        return regex.find(decodedUrl)?.groups?.get(1)?.value
    }


    override fun getAllItems(): Flow<List<ContentItem>> {
        return contentDao.getAllItems()
    }

    override fun getArchivedItems(): Flow<List<ContentItem>> {
        return contentDao.getArchivedItems()
    }

    override suspend fun saveContentFromUrl(url: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val youTubeId = extractYouTubeId(url)

                if (youTubeId != null) {
                    val response = youTubeApiService.getVideoDetails(videoId = youTubeId)
                    val videoItem = response.items.firstOrNull()
                        ?: return@withContext Result.failure(Exception("Video not found"))

                    val durationIso = videoItem.contentDetails.duration
                    val durationMinutes =
                        java.time.Duration.parse(durationIso).toMinutes().toInt().coerceAtLeast(1)

                    val contentItem = ContentItem(
                        url = url,
                        title = videoItem.snippet.title,
                        description = null,
                        imageUrl = videoItem.snippet.thumbnails.high.url,
                        sourceName = videoItem.snippet.channelTitle,
                        type = ContentType.VIDEO,
                        estimatedTimeMinutes = durationMinutes,
                        isArchived = false,
                    )

                    contentDao.insertItem(contentItem)
                    Result.success(Unit)
                } else {
                    // Fetch HTML Content from the URL with a proper User-Agent to avoid 403s
                    val doc = Jsoup.connect(url)
                        .userAgent(USER_AGENT)
                        .timeout(10000)
                        .get()

                    // Get the title
                    val title = doc.title()

                    /*** Extract the text and calculate an approximate reading time ***/
                    val siteText = doc.body().text()
                    val words = siteText.split("\\s+".toRegex()).filter { it.isNotBlank() }
                    val wordCount = words.size

                    // Refined reading time: 225 words per minute + 12 seconds per image
                    val imageCount = doc.select("img").size
                    val totalSeconds = (wordCount / 225.0 * 60) + (imageCount * 12)
                    val estimatedTimeMinutes = (totalSeconds / 60).toInt().coerceAtLeast(1)

                    // Extracting the Description
                    var description = doc.select("meta[property=og:description]").attr("content")
                    if (description.isNullOrEmpty()) {
                        description = doc.select("meta[name=description]").attr("content")
                    }

                    // Extract a representative image
                    val imageUrl = extractImageUrl(doc)
                    Log.i("Image URL", imageUrl ?: "No image URL found")

                    // Get the source name from URL
                    val sourceName = try {
                        URL(url).host.replace("www.", "")
                    } catch (e: Exception) {
                        "Web"
                    }

                    val contentItem = ContentItem(
                        url = url,
                        title = title,
                        description = description.ifEmpty { null },
                        imageUrl = imageUrl?.ifEmpty { null },
                        sourceName = sourceName,
                        type = ContentType.ARTICLE, // For now, we assume everything is an article.
                        estimatedTimeMinutes = estimatedTimeMinutes,
                        isArchived = false
                    )

                    contentDao.insertItem(contentItem)

                    Result.success(Unit)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error scraping URL: $url", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun deleteItem(item: ContentItem) {
        withContext(Dispatchers.IO) {
            contentDao.deleteItem(item)
        }
    }

    override suspend fun deleteMultipleItems(items: List<ContentItem>) {
        withContext(Dispatchers.IO) {
            contentDao.deleteMultipleItems(items)
        }
    }

    override suspend fun archiveItem(item: ContentItem) {
        withContext(Dispatchers.IO) {
            contentDao.updateItem(item.copy(isArchived = true))
        }
    }

    override suspend fun unarchiveItem(item: ContentItem) {
        withContext(Dispatchers.IO) {
            contentDao.updateItem(item.copy(isArchived = false))
        }
    }

    override suspend fun unarchiveMultipleItems(items: List<ContentItem>) {
        withContext(Dispatchers.IO) {
            items.forEach { item ->
                contentDao.updateItem(item.copy(isArchived = false))
            }
        }
    }

    override suspend fun getArticleContent(url: String): Result<ArticleContent> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Check if we have it in the database
                val localItem = contentDao.getItemByUrl(url)
                if (localItem?.cachedContent != null) {
                    Log.i(TAG, "Returning cached content for $url")
                    return@withContext Result.success(
                        ArticleContent(
                            localItem.title,
                            localItem.cachedContent
                        )
                    )
                }

                // 2. Fetch and extract if not cached
                val doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(10000)
                    .followRedirects(true)
                    .get()

                val articleContent = webContentExtractor.extractContent(url, doc)
                Log.i(TAG, "Extracted content for: ${articleContent.title}")

                // 3. Save to cache if we have a local item
                if (localItem != null && articleContent.body != null) {
                    contentDao.updateItem(localItem.copy(cachedContent = articleContent.body))
                    Log.i(TAG, "Cached content for $url saved to database")
                }

                Result.success(articleContent)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch article content for $url", e)
                Result.failure(e)
            }
        }
    }

    private fun extractImageUrl(doc: org.jsoup.nodes.Document): String? {
        // 1. Open Graph Protocol
        var imageUrl: String? = doc.select("meta[property=og:image]").attr("content")

        // 2. Twitter Card
        if (imageUrl.isNullOrEmpty()) {
            imageUrl = doc.select("meta[name=twitter:image]").attr("content")
        }

        // 3. Picture or Figure tags
        if (imageUrl.isNullOrEmpty()) {
            val pictureImg = doc.select("picture source").first()?.attr("srcset")
                ?: doc.select("figure img").first()?.attr("src")

            // Handle srcset (take the first URL before the space/descriptor)
            imageUrl = pictureImg?.split("\\s+".toRegex())?.firstOrNull()
        }

        // 4. General <img> tag fallback (ignoring common tiny icons/trackers)
        if (imageUrl.isNullOrEmpty()) {
            imageUrl = doc.select("img[src~=(?i)\\.(png|jpe?g|webp|avif)]")
                .filter { it.attr("width").let { w -> w.isEmpty() || (w.toIntOrNull() ?: 0) > 50 } }
                .firstOrNull()
                ?.attr("abs:src") // Use abs:src to get the full absolute URL
        }

        // Ensure we return null if the string is still empty or just whitespace
        return imageUrl?.takeIf { it.isNotBlank() }
    }

    companion object {
        private const val TAG = "ContentRepository"
        private const val USER_AGENT =
            "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Mobile Safari/537.36"
    }

}