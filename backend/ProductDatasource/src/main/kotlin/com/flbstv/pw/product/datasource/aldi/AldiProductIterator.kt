package com.flbstv.pw.product.datasource.aldi

import com.flbstv.pw.api.data.NULL_OBJECT
import com.flbstv.pw.api.data.Product
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.HttpStatusException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.SocketException
import java.util.*
import java.util.stream.StreamSupport


private const val SOURCE_NAME = "ALDI"
private const val VERSION = 1
private const val BASE_URL = "https://shopservice.roksh.com"

class AldiProductIterator(private val proxyHost: String, private val proxyPort: Int) : Iterator<Product> {
    private val jwtToken: String
    private val categoryIds: List<String>
    private val categoryIdIterator: Iterator<String>
    private var categoryElements: List<Map<String, Any>>
    private var categoryElementIterator: Iterator<Map<String, Any>>
    private var currentCategory = ""
    private var categoryPages = 0
    private var currentCategoryPage = 1
    private val restTemplate: RestTemplate = RestTemplate(clientHttpRequestFactory())


    var logger: Logger = LoggerFactory.getLogger(AldiProductIterator::class.java)

    init {
        jwtToken = getJwtToken()
        categoryIds = getCategoryIds()
        categoryIdIterator = categoryIds.iterator()
        categoryElements = ArrayList()
        categoryElementIterator = categoryElements.iterator()
    }

    override fun hasNext(): Boolean {
        return categoryIdIterator.hasNext() || categoryElementIterator.hasNext() || categoryPages > currentCategoryPage
    }

    override fun next(): Product {
        while (!categoryElementIterator.hasNext() && hasNext()) {
            if (currentCategoryPage < categoryPages) {
                currentCategoryPage++
            } else {
                currentCategoryPage = 1
                currentCategory = categoryIdIterator.next()
            }
            nextCategoryElements()
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
        var productId = productPropertyMap["ProductID"] as Int
        return Product(
            SOURCE_NAME,
            VERSION,
            Date(),
            productId.toString(),
            productPropertyMap["ProductName"] as String,
            productPropertyMap["ProductName"] as String,
            safeGetNumber("Price", productPropertyMap),
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

    private fun nextCategoryElements() {
        val url = "$BASE_URL/productlist/GetProductList"
        val data = mapOf("Page" to currentCategoryPage, "CategoryProgId" to currentCategory)
        logger.info("Getting {} {}", url, data)
        val httpEntity = getHttpEntity(data)
        val response = responseEntity(url, httpEntity)
        val responseObject = JSONObject(response.body)
        val productList = responseObject.get("ProductList") as JSONArray
        categoryPages = responseObject.get("TotalPages") as Int
        categoryElements = StreamSupport.stream(productList.spliterator(), false)
            .map { (it as JSONObject).toMap() }.toList()
        categoryElementIterator = categoryElements.iterator()
    }

    private fun getJwtToken(): String {
        logger.info("Getting JWT token")
        val url = "$BASE_URL/session/configure"
        val data = mapOf(
            "OwnWebshopProviderCode" to "",
            "SetUserSelectedShopsOnFirstSiteLoad" to true,
            "RedirectToDashboardNeeded" to false,
            "ShopsSelectedForRoot" to "aldi",
            "BrandProviderSelectedForRoot" to null,
            "UserSelectedShops" to emptyList<String>()
        )
        val headers = getBaseHeaders()
        val httpEntity = HttpEntity(data, headers)
        val response = restTemplate.postForEntity(url, httpEntity, String::class.java)
        val jwtToken = response.headers["jwt-auth"]?.get(0) ?: ""
        logger.info("Got JWT token")
        logger.debug(jwtToken)
        return jwtToken
    }

    private fun getCategoryIds(): List<String> {
        val url = "$BASE_URL/category/GetFullCategoryList/"
        logger.info("Getting: {}", url)
        val httpEntity = getHttpEntity(Collections.emptyMap())
        val response = restTemplate.postForEntity(url, httpEntity, String::class.java)
        val responseObject = JSONArray(response.body)
        return StreamSupport.stream(responseObject.spliterator(), false)
            .filter { (it as JSONObject).get("Status") as String == "y" }
            .map { getChildCategories(it as JSONObject) }.toList().flatten()

    }

    private fun responseEntity(
        url: String,
        httpEntity: HttpEntity<Map<String, Any>>
    ): ResponseEntity<String> {

        for (i in 1..10) {
            try {
                Thread.sleep(5000)
                System.setProperty("socksProxyVersion", "4")
                return restTemplate.postForEntity(url, httpEntity, String::class.java)
            } catch (e: HttpStatusException) {
            } catch (e: SocketException) {
            } catch (e: ResourceAccessException) {
            }

        }
        throw Exception("Could't get contet")
    }

    private fun getChildCategories(jsonObject: JSONObject): List<String> {
        val childList = jsonObject.get("ChildList") as JSONArray
        if (childList.isEmpty) {
            return listOf(jsonObject.get("ProgID") as String)
        }
        return StreamSupport.stream(childList.spliterator(), false)
            .filter { (it as JSONObject).get("Status") as String == "y" }
            .map { getChildCategories(it as JSONObject) }.toList().flatten()
    }

    private fun getHttpEntity(body: Map<String, Any>): HttpEntity<Map<String, Any>> {
        val headers = getBaseHeaders()
        headers.setBearerAuth(jwtToken)
        return HttpEntity(body, headers)
    }

    private fun getBaseHeaders(): HttpHeaders {
        return HttpHeaders().apply {
            this["user-agent"] = "Crawler"
        }
    }

    private fun clientHttpRequestFactory(): ClientHttpRequestFactory {
        System.setProperty("socksProxyVersion", "4")
        val proxy = Proxy(
            Proxy.Type.SOCKS,
            InetSocketAddress.createUnresolved(proxyHost, proxyPort)
        )
        return SimpleClientHttpRequestFactory()/*.apply {
            setProxy(proxy)
        }*/
    }
}