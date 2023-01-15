package com.flbstv.pw.api.data

data class ProductInfo(
    val source: String,
    val id: String,
    val name: String,
    val description: String,
    val price: Double
)
{
    fun uniqueId(): String = "${source}_${id}"

}