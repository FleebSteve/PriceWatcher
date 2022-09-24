package com.flbstv.pw.api.plugin

import com.flbstv.pw.api.data.Product
import java.util.stream.Stream

interface ProductProvider {

    fun getProducts(): Stream<Product>
}