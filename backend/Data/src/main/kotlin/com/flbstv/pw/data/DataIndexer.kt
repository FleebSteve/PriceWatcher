package com.flbstv.pw.data

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch._types.SuggestMode
import co.elastic.clients.elasticsearch.core.SearchRequest
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.ElasticsearchTransport
import co.elastic.clients.transport.rest_client.RestClientTransport
import com.fasterxml.jackson.databind.ObjectMapper
import com.flbstv.pw.api.const.KafkaTopics
import com.flbstv.pw.api.data.Product
import com.flbstv.pw.api.data.ProductInfo
import com.flbstv.pw.api.service.ProductSearchService
import com.flbstv.pw.ext.limit
import jakarta.annotation.PostConstruct
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service


@Service
class DataIndexer(private val objectMapper: ObjectMapper): ProductSearchService {

    private var logger: Logger = LoggerFactory.getLogger(DataIndexer::class.java)

    @Value("\${elasticsearch.host}")
    private lateinit var elasticsearchHost: String

    @Value("\${elasticsearch.port}")
    private var elasticsearchPort: Int = 0

    private lateinit var client: ElasticsearchClient

    companion object {
        const val PRODUCT_INFO_INDEX = "product_index"
        const val SUGGEST_NAME = "product_name_phrase"
    }

    @PostConstruct
    fun init() {

        val restClient = RestClient.builder(
            HttpHost(elasticsearchHost, elasticsearchPort)
        ).build()

        val transport: ElasticsearchTransport = RestClientTransport(
            restClient, JacksonJsonpMapper()
        )

        client = ElasticsearchClient(transport)
    }

    override fun search(searchTerm: String): List<ProductInfo> {
        return client.search(
            { searchRequestBuilder ->
                querySearchBuilder(searchRequestBuilder, searchTerm)
            },
            ProductInfo::class.java
        ).hits().hits().mapNotNull { it.source() }.toList()
    }

    private fun querySearchBuilder(
        searchRequestBuilder: SearchRequest.Builder,
        searchTerm: String
    ): SearchRequest.Builder? = searchRequestBuilder.index(PRODUCT_INFO_INDEX)
        .query { objectBuilder ->
            objectBuilder.queryString { qsq ->
                qsq.query(searchTerm)
                    .fields("name^2", "description")
            }
        }

    override fun suggest(searchTerm: String): List<String> {
       return client.search({
               searchRequestBuilder ->
                suggestSearchBuilder(searchRequestBuilder, searchTerm)
            },
            ProductInfo::class.java
        ).suggest()["simple_phrase"]
           ?.stream()
           ?.map { it.phrase() }
           ?.flatMap { it.options().stream() }
           ?.map { it.text() }
           ?.toList()
           ?: throw NoSuchElementException("Couldn't create any suggestion")
    }


    @KafkaListener(topics = [KafkaTopics.PRODUCT_STREAM], groupId = "product.data.indexer")
    fun consumeProductStream(message: String) {
        try {
            doConsume(message)
        }catch (e: Exception) {
            logger.error("Failed to consume message: $message", e)
        }
    }

    private fun doConsume(message: String) {
        val product = objectMapper.readValue(message, Product::class.java)
        client.index { i ->
            i
                .index(product.source.lowercase())
                .id(product.id.limit(512))
                .document(product)
        }
        val productInfo = product.toProductInfo()
        client.index { i ->
            i
                .index(PRODUCT_INFO_INDEX)
                .id(productInfo.uniqueId().limit(512))
                .document(productInfo)
        }
    }

    private fun suggestSearchBuilder(
        searchRequestBuilder: SearchRequest.Builder,
        searchTerm: String
    ): SearchRequest.Builder? = searchRequestBuilder
        .index(PRODUCT_INFO_INDEX)
        .suggest { suggestBuilder ->
            suggestBuilder.text(searchTerm)
            suggestBuilder.suggesters(
                SUGGEST_NAME
            ) { field ->
                field.phrase { ph ->
                    ph.field("name.trigram")
                        .size(5)
                        .gramSize(2)
                        .directGenerator { dg ->
                            dg.field("name.trigram")
                                .suggestMode(SuggestMode.Always)
                        }
                }
            }
        }



}