package com.flbstv.pw.api.const

enum class ProductOrder(
    private val value: String
) {
    RELEVANCE("relevance"),
    PRICE_ASC("priceAsc"),
    PRICE_DESC("priceDesc");

    companion object {
        fun crate(value: String): ProductOrder {
            return values().first { it.value == value }
        }
    }

}