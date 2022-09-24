package com.flbstv.pw.api.data

data class Product(
    val source: String,
    val version: Int,
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val raw: Any
)
