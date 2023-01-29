package com.flbstv.pw.api.data

import java.util.*


var NULL_OBJECT = Product("NULL", 0, Date(), "NULL", "NULL", "NULL", 0.0, emptyMap())

data class Product(
    val source: String,
    val version: Int,
    val date: Date,
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val raw: Map<String, Any>
) {
    fun toProductInfo(imageFileName: String): ProductInfo =
        ProductInfo(
            source,
            id,
            name,
            description,
            price,
            imageFileName
        )
}
