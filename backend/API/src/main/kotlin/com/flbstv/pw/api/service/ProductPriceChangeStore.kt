package com.flbstv.pw.api.service

import com.flbstv.pw.api.data.PricePoint
import com.flbstv.pw.api.data.ProductPriceChange
import java.util.*

interface ProductPriceChangeStore {

    fun getLastPriceChange(source: String, id: String): Optional<ProductPriceChange>

    fun getPriceChanges(source: String, id: String): List<ProductPriceChange>

    fun addPriceChange(productPriceChange: ProductPriceChange)

    fun getPricePoints(id: String): Map<String, List<PricePoint>>
}