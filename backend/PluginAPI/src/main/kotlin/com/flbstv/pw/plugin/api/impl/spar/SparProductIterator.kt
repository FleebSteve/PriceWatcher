package com.flbstv.pw.plugin.api.impl.spar

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.flbstv.pw.plugin.api.model.Product
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal

private const val SOURCE_NAME = "SPAR"
private const val PLUGIN_VERSION = 1
private const val BASE_URL =
    "https://search-spar.spar-ics.com/fact-finder/rest/v4/search/products_lmos_hu?query=*&q=*&page=1&hitsPerPage=10000&filter=category-path:"


class SparProductIterator : Iterator<Product> {
    private val categoryList: List<String>
    private val categoryIterator: Iterator<String>
    private var categoryElements: List<Map<String, Any>>
    private var categoryElementIterator: Iterator<Map<String, Any>>

    init {
        val mainPage = getDocument("https://www.spar.hu/onlineshop")
        val select = mainPage.select("a[data-level=1]")
        categoryList = select.stream().map { it.attr("href").split("/").last { t -> t.startsWith("H") } }.toList()
        categoryIterator = categoryList.iterator()
        categoryElements = ArrayList()
        categoryElementIterator = categoryElements.iterator()
    }

    override fun hasNext(): Boolean {
        return categoryIterator.hasNext() || categoryElementIterator.hasNext()
    }

    override fun next(): Product {
        if (!categoryElementIterator.hasNext()) {
            nextCategoryElements()
        }
        val productPropertyMap = categoryElementIterator.next()
        return createProductFromPropertyMap(productPropertyMap)
    }

    private fun createProductFromPropertyMap(productPropertyMap: Map<String, Any>): Product {
        val masterValues = productPropertyMap["masterValues"] as Map<String, Any>
        val title = masterValues["title"] as String
        return Product(
            SOURCE_NAME,
            PLUGIN_VERSION,
            title,
            title,
            title,
            safeGetNumber("price", masterValues),
            masterValues
        )
    }

    private fun safeGetNumber(name: String, productPropertyMap: Map<String, Any>): Double {
        var value = productPropertyMap[name]
        return when (value) {
            is Integer -> {
                value.toDouble()
            }

            is Double -> {
                value
            }

            is BigDecimal -> {
                value.toDouble()
            }

            else -> {
                -1.0
            }
        }
    }

    private fun nextCategoryElements() {
        val nextCategory = categoryIterator.next()
        val url = BASE_URL + nextCategory
        println(url)
        val restTemplate = RestTemplate()
        val response = restTemplate.getForEntity(url, String::class.java)
        val responseObject = JSONObject(response.body)
        val hits: JSONArray = responseObject.get("hits") as JSONArray
        var categoryElements = mutableListOf<Map<String, Any>>()
        for (hit in hits) {
            categoryElements.add((hit as JSONObject).toMap())
        }
        this.categoryElements = categoryElements
        this.categoryElementIterator = categoryElements.iterator()
    }

    private fun getDocument(url: String): Document {
        for (i in 1..10) {
            try {
                return Jsoup.connect(url).get()
            } catch (e: HttpStatusException) {
            }
            Thread.sleep(5000L)
        }
        throw RuntimeException("Couldn't execute request: $url")
    }
}