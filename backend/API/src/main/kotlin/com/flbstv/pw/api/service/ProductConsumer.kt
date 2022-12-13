package com.flbstv.pw.api.service

import com.flbstv.pw.api.data.Product

interface ProductConsumer {
    fun consume(id: Int, product: Product)

    fun replay(product: Product)

}