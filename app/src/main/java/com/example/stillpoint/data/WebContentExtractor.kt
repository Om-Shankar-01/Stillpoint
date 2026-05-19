package com.example.stillpoint.data

import net.dankito.readability4j.Article
import net.dankito.readability4j.extended.Readability4JExtended
import org.jsoup.nodes.Document

/**
 * Interface for extracting meaningful content (article body, title) from a web page.
 */
interface WebContentExtractor {
    fun extractContent(url: String, document: Document): ArticleContent
}

/**
 * An implementation of [WebContentExtractor] that uses the Readability algorithm
 * to strip away navigation, ads, and other "noise" from the HTML.
 */
class ReadabilityContentExtractor : WebContentExtractor {
    override fun extractContent(url: String, document: Document): ArticleContent {
        // Readability works best on a clone of the document as it modifies the DOM
        val docClone = document.clone()

        // Language selection for the purposes of TTS Speech Language
        val rawLang = docClone.select("html").attr("lang")
        val parsedLang = if (rawLang.isNotBlank()) rawLang.split("-")[0] else "en"
        
        // Remove known "noisy" elements that Readability might miss
        docClone.select("nav, footer, .ads, .sidebar, script, style").remove()

        val readability = Readability4JExtended(url, docClone)
        val article: Article = readability.parse()

        return ArticleContent(
            title = article.title ?: docClone.title(),
            body = article.content ?: article.textContent ?: "",
            languageCode = parsedLang,
        )
    }
}
