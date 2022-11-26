package com.flbstv.pw.api

import com.flbstv.pw.plugin.api.model.Product

interface ProductConsumer {
    fun consume(id: Int, product: Product)

    fun replay(product: Product)

}