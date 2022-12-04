package com.flbstv.pw.data

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.ElasticsearchTransport
import co.elastic.clients.transport.rest_client.RestClientTransport
import com.fasterxml.jackson.databind.ObjectMapper
import com.flbstv.pw.api.const.KafkaTopics
import com.flbstv.pw.plugin.api.model.Product
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service


@Service
class DataIndexer(private val objectMapper: ObjectMapper) {

    private var logger: Logger = LoggerFactory.getLogger(DataIndexer::class.java)

    @Value("\${elasticsearch.host}")
    lateinit var elasticsearchHost: String

    @Value("\${elasticsearch.port}")
    var elasticsearchPort: Int = 0

    private val client: ElasticsearchClient

    init {

        val restClient = RestClient.builder(
            HttpHost(elasticsearchHost, elasticsearchPort)
        ).build()

        val transport: ElasticsearchTransport = RestClientTransport(
            restClient, JacksonJsonpMapper()
        )

        client = ElasticsearchClient(transport)
    }


    @KafkaListener(topics = [KafkaTopics.PRODUCT_STREAM], groupId = "product.data.indexer")
    fun consumeProductStream(message: String) {
        val product = objectMapper.readValue(message, Product::class.java)
        client.index { i ->
            i
                .index(product.source.lowercase())
                .id(product.id)
                .document(product)
        }
    }
}