package com.flbstv.pw.api.service

import com.flbstv.pw.api.data.Product
import java.util.stream.Stream

interface ProductStore {
    fun store(id: Int, product: Product)

    fun findAll() : Stream<Product>
}