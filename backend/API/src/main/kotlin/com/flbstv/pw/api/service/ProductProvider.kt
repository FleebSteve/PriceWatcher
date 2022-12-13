package com.flbstv.pw.api.service

import com.flbstv.pw.api.data.Product
import java.util.stream.Stream

interface ProductProvider {

    fun getProducts(): Stream<Product>

    fun imageUrl(product: Product): String
}