package com.example.stillpoint.ui.readerscreen

import org.jsoup.Jsoup
import org.jsoup.nodes.Element

sealed class HtmlBlock {
    data class Header(val text: String, val level: Int) : HtmlBlock()
    data class Paragraph(val html: String) : HtmlBlock()
    data class Image(val url: String, val caption: String? = null) : HtmlBlock()
    data class BlockQuote(val html: String) : HtmlBlock()
    data class ListItem(val html: String, val isOrdered: Boolean, val index: Int) : HtmlBlock()
}

object HtmlParser {
    /**
     * Parses HTML content into a list of [HtmlBlock]s.
     * @param html The HTML content string.
     * @param baseUrl The base URL of the page, used to resolve relative links and image paths.
     */
    fun parse(html: String?, baseUrl: String? = null): List<HtmlBlock> {
        if (html.isNullOrBlank()) return emptyList()
        
        val doc = if (baseUrl != null) {
            Jsoup.parseBodyFragment(html, baseUrl)
        } else {
            Jsoup.parseBodyFragment(html)
        }
        
        val blocks = mutableListOf<HtmlBlock>()
        
        // We select the main content containers
        val elements = doc.body().select("h1, h2, h3, h4, h5, h6, p, img, blockquote, ul, ol, figure")
        
        elements.forEach { element ->
            // Avoid duplicate processing if an element is inside another we already handled
            if (element.parents().any { it.tagName() in listOf("p", "blockquote", "li", "figure") }) {
                return@forEach
            }

            when (element.tagName()) {
                "h1", "h2", "h3", "h4", "h5", "h6" -> {
                    val level = element.tagName().substring(1).toInt()
                    blocks.add(HtmlBlock.Header(element.text(), level))
                }
                "p" -> {
                    // Check for images inside the paragraph
                    val images = element.select("img")
                    if (images.isNotEmpty()) {
                        images.forEach { img ->
                            val src = getBestImageUrl(img, baseUrl)
                            if (src != null) {
                                blocks.add(HtmlBlock.Image(src, img.attr("alt")))
                            }
                        }
                    }
                    
                    val elementClone = element.clone()
                    elementClone.select("img").remove()
                    if (elementClone.text().isNotBlank()) {
                        blocks.add(HtmlBlock.Paragraph(elementClone.html()))
                    }
                }
                "img" -> {
                    val src = getBestImageUrl(element, baseUrl)
                    if (src != null) {
                        blocks.add(HtmlBlock.Image(src, element.attr("alt")))
                    }
                }
                "figure" -> {
                    val img = element.selectFirst("img")
                    val caption = element.selectFirst("figcaption")?.text() ?: img?.attr("alt")
                    if (img != null) {
                        val src = getBestImageUrl(img, baseUrl)
                        if (src != null) {
                            blocks.add(HtmlBlock.Image(src, caption))
                        }
                    }
                }
                "blockquote" -> {
                    blocks.add(HtmlBlock.BlockQuote(element.html()))
                }
                "ul", "ol" -> {
                    val isOrdered = element.tagName() == "ol"
                    element.children().forEachIndexed { index, li ->
                        if (li.tagName() == "li") {
                            blocks.add(HtmlBlock.ListItem(li.html(), isOrdered, index + 1))
                        }
                    }
                }
            }
        }
        
        return blocks
    }

    private fun getBestImageUrl(img: Element, baseUrl: String?): String? {
        // Try various attributes where the URL might hide (lazy loading)
        val attrs = listOf("src", "data-src", "data-original-src", "data-lazy-src", "srcset")
        
        for (attr in attrs) {
            val value = if (baseUrl != null) img.absUrl(attr) else img.attr(attr)
            
            if (value.isNotBlank()) {
                if (attr == "srcset") {
                    return value.split(",").firstOrNull()?.trim()?.split(" ")?.firstOrNull()
                }
                return value
            }
        }
        return null
    }
}
