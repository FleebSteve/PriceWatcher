package com.flbstv.pw.store.product.datasource

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface ProductDatasourceRunLogRepository : MongoRepository<ProductDatasourceRunLog, ObjectId> {
}