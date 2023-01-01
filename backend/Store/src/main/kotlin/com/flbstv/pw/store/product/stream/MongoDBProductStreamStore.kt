package com.flbstv.pw.store.product.stream

import com.flbstv.pw.api.data.Product
import com.flbstv.pw.api.service.ProductStore
import org.bson.types.ObjectId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.stream.Stream

@Service
class MongoDBProductStreamStore(private val repository: ProductStreamRepository) : ProductStore {

    var logger: Logger = LoggerFactory.getLogger(MongoDBProductStreamStore::class.java)

    override fun store(id: Int, product: Product) {
        logger.debug("Consuming: {}/{}", product.source, product.id)
        val productEntity = ProductStream(
            ObjectId(),
            id,
            product.source,
            product.version,
            product.id,
            product.date,
            product.name,
            product.description,
            product.price,
            product.raw
        )
        repository.save(productEntity)
        logger.debug("Consumed: {}/{}", product.source, product.id)
    }

    override fun findAll(): Stream<Product> {
        return repository.findAllByOrderByDate().map { toProduct(it) }
    }

    private fun toProduct(productStream: ProductStream): Product {
        return Product(
            productStream.source,
            productStream.version,
            productStream.date,
            productStream.productId,
            productStream.name,
            productStream.description,
            productStream.price,
            productStream.raw
        )
    }
}