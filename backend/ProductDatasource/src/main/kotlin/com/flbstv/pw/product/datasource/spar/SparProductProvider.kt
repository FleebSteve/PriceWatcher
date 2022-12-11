package com.flbstv.pw.product.datasource.spar

import com.flbstv.pw.api.data.Product
import com.flbstv.pw.api.service.ProductProvider
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport

class SparProductProvider : ProductProvider {

    override fun getProducts(): Stream<Product> {
        var iterator = SparProductIterator()
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.DISTINCT), false)
    }

    override fun imageUrl(product: Product): String {
        return product.raw["image-url"] as String
    }
}