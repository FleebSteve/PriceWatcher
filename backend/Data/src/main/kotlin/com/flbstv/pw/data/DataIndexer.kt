package com.flbstv.pw.data

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.ElasticsearchTransport
import co.elastic.clients.transport.rest_client.RestClientTransport
import com.fasterxml.jackson.databind.ObjectMapper
import com.flbstv.pw.api.const.KafkaTopics
import com.flbstv.pw.api.data.Product
import jakarta.annotation.PostConstruct
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
    private lateinit var elasticsearchHost: String

    @Value("\${elasticsearch.port}")
    private var elasticsearchPort: Int = 0

    private lateinit var client: ElasticsearchClient

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