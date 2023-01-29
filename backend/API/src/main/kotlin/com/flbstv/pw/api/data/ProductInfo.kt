package com.flbstv.pw.api.data

import com.fasterxml.jackson.annotation.JsonProperty

data class ProductInfo(
    @JsonProperty("source")  val source: String,
    @JsonProperty("id")  val id: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("description") val description: String,
    @JsonProperty("price")  val price: Double,
    @JsonProperty("image")  val image: String
)
{
    fun uniqueId(): String = "${source}_${id}"

}