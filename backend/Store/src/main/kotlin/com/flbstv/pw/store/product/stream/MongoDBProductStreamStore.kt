package com.flbstv.pw.store.product.stream

import com.flbstv.pw.api.ProductConsumer
import com.flbstv.pw.plugin.api.model.Product
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import java.util.*

@Service
class MongoDBProductStreamStore(private val repository: ProductStreamRepository) : ProductConsumer {

    override fun consume(product: Product) {
        val productEntity = ProductStream(
            ObjectId(),
            product.source,
            product.version,
            product.id,
            Date(),
            product.description,
            product.price,
            product.raw
        )
        repository.save(productEntity)
    }
}