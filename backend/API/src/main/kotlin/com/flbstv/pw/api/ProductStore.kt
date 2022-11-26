package com.flbstv.pw.api

import com.flbstv.pw.plugin.api.model.Product
import java.util.stream.Stream

interface ProductStore {
    fun store(id: Int, product: Product)

    fun findAll() : Stream<Product>
}