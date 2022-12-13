package com.flbstv.application.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.flbstv.pw.api.const.KafkaTopics
import com.flbstv.pw.api.data.Product
import com.flbstv.pw.api.service.ProductConsumer
import com.flbstv.pw.api.service.ProductStore
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class ProductConsumerImpl(
    private val productStore: ProductStore,
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) : ProductConsumer {

    var logger: Logger =  LoggerFactory.getLogger(ProductConsumerImpl::class.java)

    override fun consume(id: Int, product: Product) {
        logger.debug("Consuming: $product")
        productStore.store(id, product)
        kafkaTemplate.send(KafkaTopics.PRODUCT_STREAM, objectMapper.writeValueAsString(product))
        logger.debug("Consumed: $product")
    }

    override fun replay(product: Product) {
        logger.debug("Replaying: $product")
        kafkaTemplate.send(KafkaTopics.PRODUCT_STREAM, objectMapper.writeValueAsString(product))
        logger.debug("Replayed: $product")
    }
}