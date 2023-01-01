package com.flbstv.pw.api.data

import java.util.*

data class ProductPriceChange(
    val source: String,
    val id: String,
    val date: Date,
    val name: String,
    val currentPrice: Double,
    val lastKnownPrice: Double,
    val firstKnownPrice: Double,
    val changeSinceLastPrice: Double,
    val changeSinceFirstKnownPrice: Double,
    val changeSinceLastPriceRatio: Double,
    val changeSinceFirstKnownPriceRatio: Double
)
