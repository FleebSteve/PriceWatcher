package com.flbstv.pw.api

import com.flbstv.pw.api.data.Product

interface ProductConsumer {
    fun consume(product: Product)
}