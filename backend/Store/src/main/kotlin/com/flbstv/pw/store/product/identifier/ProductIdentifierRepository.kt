package com.flbstv.pw.store.product.identifier

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface ProductIdentifierRepository: MongoRepository<ProductIdentifier, ObjectId> {

    fun findByName(name: String): ProductIdentifier?
}