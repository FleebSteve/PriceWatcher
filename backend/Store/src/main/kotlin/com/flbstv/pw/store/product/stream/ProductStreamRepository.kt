package com.flbstv.pw.store.product.stream

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.stream.Stream

@Repository
interface ProductStreamRepository : MongoRepository<ProductStream, ObjectId> {

    fun findAllByOrderByDate() : Stream<ProductStream>

}