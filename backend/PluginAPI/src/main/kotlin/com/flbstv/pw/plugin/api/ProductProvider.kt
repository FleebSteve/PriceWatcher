package com.flbstv.pw.plugin.api

import com.flbstv.pw.plugin.api.model.Product
import java.util.stream.Stream

interface ProductProvider {

    fun getProducts(): Stream<Product>

    fun imageUrl(product: Product): String
}