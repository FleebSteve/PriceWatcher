package com.flbstv.pw.api

import com.flbstv.pw.plugin.api.model.Product

interface ProductConsumer {
    fun consume(product: Product)
}