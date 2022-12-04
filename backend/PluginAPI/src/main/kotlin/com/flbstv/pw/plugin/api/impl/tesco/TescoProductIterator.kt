package com.flbstv.pw.plugin.api.impl.tesco

import com.flbstv.pw.plugin.api.model.Product
import com.jayway.jsonpath.JsonPath
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.net.Proxy
import kotlin.math.ceil

private const val SOURCE_NAME = "TESCO"
private const val PLUGIN_VERSION = 1

class TescoProductIterator(private val proxyHost: String, private val proxyPort: Int) : Iterator<Product> {
    private val categoryUrls: List<String>
    private val categoryIterator: Iterator<String>
    private var currentCategoryUrl: String = ""
    private var categoryElements: List<Map<String, Any>>
    private var categoryElementIterator: Iterator<Map<String, Any>>
    private var currentCategoryPage = 0
    private var numberOfCategoryPages = 0

    var logger: Logger =  LoggerFactory.getLogger(TescoProductIterator::class.java)


    init {
        categoryUrls = categoryUrls()
        categoryIterator = categoryUrls.iterator()
        categoryElements = ArrayList()
        categoryElementIterator = categoryElements.iterator()
    }

    override fun hasNext(): Boolean {
        return categoryIterator.hasNext() || categoryElementIterator.hasNext() || currentCategoryPage < numberOfCategoryPages;
    }

    override fun next(): Product {
        if (!categoryElementIterator.hasNext()) {
            if (currentCategoryPage < numberOfCategoryPages) {
                readNextPageInCategory()
            } else {
                readNextCategory()
            }
        }
        val productPropertyMap = categoryElementIterator.next()
        val nextElement = createProductFromPropertyMap(productPropertyMap)
        logger.debug("Next element: {}", nextElement.id)
        logger.trace("Value: {}", nextElement)
        return nextElement
    }

    private fun readNextPageInCategory() {
        val document = getDocument(currentCategoryUrl, ++currentCategoryPage)
        readElements(document)
    }

    private fun readNextCategory() {
        currentCategoryUrl = categoryIterator.next()
        val document = getDocument(currentCategoryUrl)
        val bodyDataPropsValue = document.body().attr("data-props")
        val pageInformation = JsonPath.read<Map<String, Any>>(
            bodyDataPropsValue,
            "$.resources.productsByCategory.data.results.pageInformation"
        )
        currentCategoryPage = 1
        val totalCount = pageInformation["totalCount"] as Int
        val pageSize = pageInformation["pageSize"] as Int
        if (totalCount != null && pageSize != null) {
            numberOfCategoryPages = ceil(totalCount.toDouble() / pageSize.toDouble()).toInt()
        }
        readElements(document)
    }

    private fun readElements(document: Document) {
        val bodyDataPropsValue = document.body().attr("data-props")
        val products = JsonPath.read<net.minidev.json.JSONArray>(
            bodyDataPropsValue,
            "$.resources.productsByCategory.data.results.productItems"
        )
        categoryElements =
            products.stream().map { it as Map<String, Any> }.map { it["product"] as Map<String, Any> }.toList()
        categoryElementIterator = categoryElements.iterator()
    }

    private fun createProductFromPropertyMap(productPropertyMap: Map<String, Any>): Product {
        logger.debug("Creating product: {}", productPropertyMap)
        val title = safeGet("title", productPropertyMap) as String
        var price = safeGetNumber("price", productPropertyMap)
        return Product(
            SOURCE_NAME,
            PLUGIN_VERSION,
            title,
            title,
            safeGet("shortDescription", productPropertyMap) as String,
            price,
            productPropertyMap
        )
    }

    private fun safeGetNumber(name: String, productPropertyMap: Map<String, Any>): Double {
        var value = safeGet(name, productPropertyMap)
        if (value is Integer) {
            return value.toDouble()
        } else if (value is Double) {
            return value
        }
        return -1.0
    }

    private fun safeGet(name: String, productPropertyMap: Map<String, Any>): Any {
        return productPropertyMap[name] ?: return ""

    }

    private fun categoryUrls(): List<String> {
        val baseUrl = "https://bevasarlas.tesco.hu/groceries/hu-HU/shop"
        val mainPage = getDocument(baseUrl)
        val bodyDataPropsValue = mainPage.body().attr("data-props")
        val categories = JsonPath.read<net.minidev.json.JSONArray>(bodyDataPropsValue, "$.resources.taxonomy.data")
        return categories.stream().map { it as Map<String, String> }.map { baseUrl + it["url"] + "/all" }.toList()
    }

    private fun getDocument(url: String, pageNumber: Int): Document {
        return getDocument("$url?page=$pageNumber")
    }

    private fun getDocument(url: String): Document {
        logger.info("Getting: {}", url)
        val proxy = Proxy(
            Proxy.Type.SOCKS,
            InetSocketAddress.createUnresolved(proxyHost, proxyPort)
        );
        for (i in 1..10) {
            try {
                Thread.sleep(30000)
                System.setProperty("socksProxyVersion", "4")
                return Jsoup.connect(url).timeout(60000).proxy(proxy).header(
                    "user-agent",
                    "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Mobile Safari/537.36"
                )
                    .get()
            } catch (e: HttpStatusException) {
            }
            Thread.sleep(5000L)
        }
        throw RuntimeException("Couldn't execute request: $url")
    }
}