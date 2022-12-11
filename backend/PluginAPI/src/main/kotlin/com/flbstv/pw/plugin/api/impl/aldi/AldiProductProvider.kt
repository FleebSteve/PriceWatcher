package com.flbstv.pw.plugin.api.impl.aldi

import com.flbstv.pw.api.data.Product
import com.flbstv.pw.api.service.ProductProvider
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport

class AldiProductProvider : ProductProvider {

    override fun getProducts(): Stream<Product> {
        var iterator = AldiProductIterator()
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.DISTINCT), false)
    }

    override fun imageUrl(product: Product): String {
        return product.raw["MediaUrlM"] as String
    }
}