package com.flbstv.pw.store.product.stream

import com.flbstv.pw.api.ProductStore
import com.flbstv.pw.plugin.api.model.Product
import org.bson.types.ObjectId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class MongoDBProductStreamStore(private val repository: ProductStreamRepository) : ProductStore {

    var logger: Logger =  LoggerFactory.getLogger(MongoDBProductStreamStore::class.java)

    override fun store(id: Int, product: Product) {
        logger.debug("Consuming: {}/{}", product.source, product.id)
        val productEntity = ProductStream(
            ObjectId(),
            id,
            product.source,
            product.version,
            product.id,
            Date(),
            product.description,
            product.price,
            product.raw
        )
        repository.save(productEntity)
        logger.debug("Consumed: {}/{}", product.source, product.id)
    }
}