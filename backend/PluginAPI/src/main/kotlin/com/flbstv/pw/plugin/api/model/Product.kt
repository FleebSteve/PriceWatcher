package com.flbstv.pw.plugin.api.model


var NULL_OBJECT = Product("NULL", 0, "NULL", "NULL", "NULL", 0.0, emptyMap())

data class Product(
    val source: String,
    val version: Int,
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val raw: Map<String, Any>
)
