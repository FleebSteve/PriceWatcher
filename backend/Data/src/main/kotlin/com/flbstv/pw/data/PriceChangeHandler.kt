package com.flbstv.pw.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.flbstv.pw.api.const.KafkaTopics
import com.flbstv.pw.api.data.Product
import com.flbstv.pw.api.data.ProductPriceChange
import com.flbstv.pw.api.service.ProductPriceChangeStore
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class PriceChangeHandler(
    private val productPriceChangeStore: ProductPriceChangeStore,
    private val objectMapper: ObjectMapper
) {

    var logger: Logger = LoggerFactory.getLogger(PriceChangeHandler::class.java)


    @KafkaListener(topics = [KafkaTopics.PRODUCT_STREAM], groupId = "product.price.handler")
    fun consumeProductStream(message: String) {
        val product = objectMapper.readValue(message, Product::class.java)
        val lastPriceChange = productPriceChangeStore.getLastPriceChange(product.source, product.id)
        if (lastPriceChange.isEmpty) {
            val productPriceChange = ProductPriceChange(
                product.source,
                product.id,
                product.date,
                product.name,
                product.price,
                product.price,
                product.price,
                0.0,
                0.0,
                0.0,
                0.0
            )
            productPriceChangeStore.addPriceChange(productPriceChange)
        } else {
            val lastPriceChangeValue = lastPriceChange.get()
            if (lastPriceChangeValue.lastKnownPrice != product.price && product.date.after(lastPriceChangeValue.date)) {
                val productPriceChange = ProductPriceChange(
                    product.source,
                    product.id,
                    product.date,
                    product.name,
                    product.price,
                    lastPriceChangeValue.currentPrice,
                    lastPriceChangeValue.firstKnownPrice,
                    product.price - lastPriceChangeValue.lastKnownPrice,
                    product.price - lastPriceChangeValue.firstKnownPrice,
                    product.price / lastPriceChangeValue.lastKnownPrice,
                    product.price / lastPriceChangeValue.firstKnownPrice,
                )
                productPriceChangeStore.addPriceChange(productPriceChange)
            }
        }
    }
}