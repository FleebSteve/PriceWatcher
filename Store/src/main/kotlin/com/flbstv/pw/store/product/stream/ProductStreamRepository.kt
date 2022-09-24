package com.flbstv.pw.store.product.stream

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductStreamRepository : MongoRepository<ProductStream, ObjectId> {

}