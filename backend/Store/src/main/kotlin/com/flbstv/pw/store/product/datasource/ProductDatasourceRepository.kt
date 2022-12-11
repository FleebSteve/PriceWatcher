package com.flbstv.pw.store.product.datasource

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository


@Repository
interface ProductDatasourceRepository : MongoRepository<ProductDatasourceStateData, ObjectId> {

    fun findByName(name: String): ProductDatasourceStateData

    fun existsByName(name: String): Boolean
}