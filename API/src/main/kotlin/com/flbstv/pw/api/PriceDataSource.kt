package com.flbstv.pw.api

import com.flbstv.pw.api.data.Product
import java.util.stream.Stream

interface PriceDataSource {

    fun getProducts(): Stream<Product>
}