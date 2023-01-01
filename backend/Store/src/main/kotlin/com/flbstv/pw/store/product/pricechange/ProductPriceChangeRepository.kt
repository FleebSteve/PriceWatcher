package com.flbstv.pw.store.product.pricechange

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductPriceChangeRepository : MongoRepository<ProductPriceChangeDocument, ObjectId>  {

    fun findBySourceAndProductId(source: String, productId: String): List<ProductPriceChangeDocument>

    fun findFirstBySourceAndProductIdOrderByDate(source: String, productId: String): ProductPriceChangeDocument?
}