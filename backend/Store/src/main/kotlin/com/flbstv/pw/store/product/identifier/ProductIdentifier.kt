package com.flbstv.pw.store.product.identifier

import com.flbstv.pw.api.data.ProductIdentifier
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document("product_identifier")
data class ProductIdentifier(
    @Id val id: ObjectId,
    @Indexed(unique = true) val name: String,
    val idMap: MutableMap<String, String>
) {
    fun toModel(): ProductIdentifier {
        return ProductIdentifier(name, idMap)
    }
}