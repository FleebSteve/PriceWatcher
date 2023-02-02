package com.flbstv.pw.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.flbstv.pw.api.const.KafkaTopics
import com.flbstv.pw.api.data.Product
import com.flbstv.pw.api.service.ProductIdentifierService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class ProductIdentityMatcher(
    private val productIdentifierService: ProductIdentifierService,
    private val objectMapper: ObjectMapper
) {

    @KafkaListener(topics = [KafkaTopics.PRODUCT_STREAM], groupId = "product.matcher")
    fun consumeProductStream(message: String) {
        val product = objectMapper.readValue(message, Product::class.java)
        productIdentifierService.add(product.name, product.source, product.id)
    }
}