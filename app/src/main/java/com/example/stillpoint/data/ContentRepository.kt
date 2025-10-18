package com.example.stillpoint.data

import android.util.Log
import com.example.stillpoint.data.local.ContentDao
import com.example.stillpoint.data.local.ContentItem
import com.example.stillpoint.data.local.ContentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException

data class ArticleContent(val title: String, val body: String)

interface ContentRepository {
    fun getAllItems() : Flow<List<ContentItem>>
    fun getArchivedItems() : Flow<List<ContentItem>>
    suspend fun saveContentFromUrl(url: String) : Result<Unit>
    suspend fun deleteItem(item: ContentItem)
    suspend fun deleteMultipleItems(items: List<ContentItem>)
    suspend fun archiveItem(item: ContentItem)
    suspend fun getArticleContent(url: String) : Result<ArticleContent>
}

class CachingContentRepository(private val contentDao: ContentDao) : ContentRepository {
    override fun getAllItems(): Flow<List<ContentItem>> {
        return contentDao.getAllItems()
    }

    override fun getArchivedItems(): Flow<List<ContentItem>> {
        return contentDao.getArchivedItems()
    }

    override suspend fun saveContentFromUrl(url: String) : Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Fetch HTML Content from the URL
                val doc = Jsoup.connect(url).get()
                // Get the title
                val title = doc.title()

                /*** Extract the text and calculate an approximate reading time ***/
                val siteText = doc.body().text()
                val wordCount = siteText.split("\\s+".toRegex()).size
                val estimatedTimeMinutes = (wordCount / 230).coerceAtLeast(1)

                // Extracting the Description
                var description = doc.select("meta[property=og:description]").attr("content")
                if (description.isNullOrEmpty()) {
                    description = doc.select("meta[name=description]").attr("content")
                }

                // Extract a representative image from the Open Graph protocol
                val imageUrl = doc.select("meta[property=og:image]").attr("content")
                Log.i("Image URL", imageUrl)

                // Get the source name from URL
                val sourceName = URL(url).host.replace("www.", "")

                val contentItem = ContentItem(
                    url = url,
                    title = title,
                    description = description.ifEmpty { null },
                    imageUrl = imageUrl.ifEmpty { null },
                    sourceName = sourceName,
                    type = ContentType.ARTICLE, // For now, we assume everything is an article.
                    estimatedTimeMinutes = estimatedTimeMinutes,
                    isArchived = false
                )

                contentDao.insertItem(contentItem)

                Result.success(Unit)
            } catch (e : Exception) {
                Log.e(TAG, "Error scraping URL: $url", e)
                when (e) {
                    is HttpStatusException -> {
                        Log.w(TAG, "HTTP Error fetching URL. Status=${e.statusCode}. Is the link correct?")
                    }
                    is SocketTimeoutException -> {
                        Log.w(TAG, "Connection timed out. Check internet connection.")
                    }
                    is UnknownHostException -> {
                        Log.w(TAG, "Could not resolve host. Check for typos in URL.")
                    }
                }

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

    override suspend fun getArticleContent(url: String): Result<ArticleContent> {
        return withContext(Dispatchers.IO) {
            try {
                val doc = Jsoup.connect(url).get()
                val title = doc.title()
                // TODO: Extract the article content from the HTML in a better fashion
                val body = doc.body().text()
                Result.success(ArticleContent(title, body))
            } catch (e: Exception) {
                Log.e("ContentRepository", "Failed to fetch article content for $url", e)
                Result.failure(e)
            }
        }
    }

    companion object {
        private const val TAG = "ContentRepository"
    }

}