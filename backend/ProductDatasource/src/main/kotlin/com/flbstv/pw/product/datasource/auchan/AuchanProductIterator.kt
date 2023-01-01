package com.flbstv.pw.product.datasource.auchan

import com.flbstv.pw.api.data.NULL_OBJECT
import com.flbstv.pw.api.data.Product
import org.json.JSONArray
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import java.util.*
import java.util.stream.StreamSupport


private const val SOURCE_NAME = "AUCHAN"
private const val VERSION = 1
private const val BASE_URL = "https://online.auchan.hu"

class AuchanProductIterator : Iterator<Product> {

    private val jwtToken: String
    private val categoryIds: List<Int>
    private val categoryIdIterator: Iterator<Int>
    private var categoryElements: List<Map<String, Any>>
    private var categoryElementIterator: Iterator<Map<String, Any>>
    private val restTemplate: RestTemplate = RestTemplate()

    var logger: Logger = LoggerFactory.getLogger(AuchanProductIterator::class.java)

    init {
        jwtToken = getJwtToken()
        categoryIds = getCategoryIds()
        categoryIdIterator = categoryIds.iterator()
        categoryElements = ArrayList()
        categoryElementIterator = categoryElements.iterator()
    }


    override fun hasNext(): Boolean {
        return categoryElementIterator.hasNext() || categoryIdIterator.hasNext()
    }

    override fun next(): Product {
        while (!categoryElementIterator.hasNext() && hasNext()) {
            var categoryId = categoryIdIterator.next()
            for (i in 1..3) {
                try {
                    nextCategoryElements(categoryId)
                    break
                } catch (e: Exception) {
                    logger.warn("Failed to get data: {}", e.message)
                    Thread.sleep(1000)
                }
            }
        }
        if (!hasNext()) {
            return NULL_OBJECT
        }
        val productPropertyMap = categoryElementIterator.next()
        val nextElement = createProductFromPropertyMap(productPropertyMap)
        logger.debug("Next element: {}", nextElement.id)
        logger.trace("Value: {}", nextElement)
        return nextElement
    }

    private fun createProductFromPropertyMap(productPropertyMap: Map<String, Any>): Product {
        var propertyMap = productPropertyMap["selectedVariant"] as Map<String, Any>
        var productId = propertyMap["id"] as Int
        var priceMap = propertyMap["price"] as Map<String, Any>
        return Product(
            SOURCE_NAME,
            VERSION,
            Date(),
            productId.toString(),
            propertyMap["name"] as String,
            propertyMap["name"] as String,
            safeGetNumber("gross", priceMap),
            productPropertyMap
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

    private fun nextCategoryElements(categoryId: Int) {
        var url = "$BASE_URL/api/v2/products?categoryId={categoryId}&itemsPerPage={itemsPerPage}&page={page}"
        var params = mapOf("categoryId" to categoryId, "itemsPerPage" to 15000, "page" to 1)
        logger.info("Getting: {} {}", url, params)
        val httpEntity = getHttpEntity()
        val response = restTemplate.exchange(
            url, HttpMethod.GET, httpEntity, Map::class.java, params
        )
        var responseObject = JSONObject(response.body)
        var results = responseObject.get("results") as JSONArray
        categoryElements = StreamSupport.stream(results.spliterator(), false)
            .map { (it as JSONObject).toMap() }.toList()
        categoryElementIterator = categoryElements.iterator()

    }

    private fun getCategoryIds(): List<Int> {
        val url = "$BASE_URL/api/v2/tree/0"
        logger.info("Getting: {}", url)
        val httpEntity = getHttpEntity()
        val response = restTemplate.exchange(
            url, HttpMethod.GET, httpEntity, String::class.java, emptyMap<String, String>()
        )
        val responseObject = JSONObject(response.body)
        val children = responseObject.get("children") as JSONArray
        return StreamSupport.stream(children.spliterator(), false)
            .map { getChildCategories(it as JSONObject) }.toList().flatten()

    }

    private fun getChildCategories(jsonObject: JSONObject): List<Int> {
        val childList = jsonObject.get("children") as JSONArray
        if (childList.isEmpty) {
            return listOf(jsonObject.get("id") as Int)
        }
        return StreamSupport.stream(childList.spliterator(), false)
            .map { getChildCategories(it as JSONObject) }.toList().flatten()
    }

    private fun getHttpEntity(): HttpEntity<Void> {
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        return HttpEntity(headers)
    }

    private fun getJwtToken(): String {
        logger.info("Getting JWT token")
        val url = "$BASE_URL"

        val response = restTemplate.getForEntity(url, String::class.java)
        val headerValues = response.headers["set-cookie"]
        if (headerValues != null) {
            val jwtToken =
                headerValues.stream()
                    .filter { it.startsWith("access_token=") }
                    .map { it.replace("access_token=", "") }
                    .findFirst().get().split(";")[0]
            logger.info("Got JWT token")
            logger.debug(jwtToken)
            return jwtToken
        }
        throw RuntimeException("Couldn't get JWT token")
    }
}