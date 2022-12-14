package com.flbstv.pw.store.product.stream

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("product_stream")
data class ProductStream(
    @Id val id: ObjectId,
    @Indexed(unique = false) val runId: Int,
    @Indexed(unique = false) val source: String,
    @Indexed(unique = false) val version: Int,
    @Indexed(unique = false) val productId: String,
    @Indexed(unique = false) val date: Date,
    @Indexed(unique = false) val name: String,
    val description: String,
    val price: Double,
    val raw: Map<String, Any>
)
